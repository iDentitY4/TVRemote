package de.sksystems.tvremote;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Consumer;

import de.sksystems.tvremote.entity.Channel;
import de.sksystems.tvremote.dao.ChannelDao;
import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.http.HttpRequestAsync;
import de.sksystems.tvremote.http.HttpRequestParamTask;
import de.sksystems.tvremote.ui.RemoteModeActivity;
import de.sksystems.tvremote.util.Callable;

/**
 * Created by Manuel on 07.12.2017.
 */

public class RemoteController {

    private static final String TV_STATE_FILENAME = "TV_STATE";

    private static final String SELECT_CHANNEL = "channelMain=";

    private static RemoteController instance;

    public static RemoteController getInstance(Context context) {
        if(instance == null) {
            instance = new RemoteController(context);
        }
        else {
            instance.setContext(context);
        }

        return instance;
    }

    protected Context mContext;

    protected SharedPreferences mPreferences;

    protected AppDatabase mDb;

    protected TVState tvState;

    protected HttpRequestAsync mRunningTask = null;

    private RemoteController(Context context) {
        mContext = context;
        mDb = Room.databaseBuilder(mContext.getApplicationContext(),
                AppDatabase.class, "tvremote-db").build();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        tvState = loadLastState();
    }

    public TVState getTVState() {
        return tvState;
    }

    private TVState loadLastState() {
        TVState state = null;
        try {
            FileInputStream fis = mContext.openFileInput(TV_STATE_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);

            Log.i("TVRemote", "Trying to load TV state from file");

            state = (TVState) ois.readObject();

            ois.close();
            fis.close();
        }
        catch(IOException | ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }

        if(state == null) {
            //No state file found, assume fresh tv state
            state = new TVState();
        }

        return state;
    }

    public void storeTVState() {
        try {
            FileOutputStream fos = mContext.openFileOutput(TV_STATE_FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            Log.i("TVRemote", "Saving TV state to file");

            oos.writeObject(tvState);
            oos.flush();
            oos.close();
            fos.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public HttpRequestAsync getRunningTask() {
        if(mRunningTask != null && mRunningTask.getStatus() == AsyncTask.Status.FINISHED) {
            mRunningTask = null;
        }
        return mRunningTask;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    private void httpRequest(String request, final Callable onSuccessCallable) {
        if(mRunningTask == null) {
            String ip = mPreferences.getString(SharedPreferencesKeys.TV.IP, null);
            int timeout = Integer.parseInt(mPreferences.getString(SharedPreferencesKeys.TV.TIMEOUT, "6000"));

            mRunningTask = new HttpRequestParamTask(ip, timeout);
            mRunningTask.setFailureListener(new HttpRequestAsync.FailureListener() {
                @Override
                public void onFailure(Exception e) {
                    removeRunningTask();
                    Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    updateActivityControlbar();
                }
            });
            mRunningTask.setSuccessListener(new HttpRequestAsync.SuccessListener() {
                @Override
                public void onSuccess() {
                    removeRunningTask();
                    if(onSuccessCallable != null) {
                        onSuccessCallable.call();
                    }
                    updateActivityControlbar();
                }
            });
            mRunningTask.setCancelledListener(new HttpRequestAsync.CancelledListener() {
                @Override
                public void onCancelled() {
                    removeRunningTask();
                    updateActivityControlbar();
                }
            });
            mRunningTask.execute(new String[]{request});
        }
        else
        {
            Toast.makeText(mContext, "Bitte warten...", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateActivityControlbar() {
        if(mContext instanceof RemoteModeActivity) {
            ((RemoteModeActivity) mContext).updateControlbar();
        }
    }

    private void removeRunningTask() {
        mRunningTask = null;
    }

    public void selectChannel(Channel channel) {
        if (tvState.pip) {
            httpRequest("channelPip=" + channel.getChannel(), null);
        } else {
            httpRequest("channelMain=" + channel.getChannel(), null);
        }
    }

    public void zoomMain() {
        if (tvState.pip) {
            httpRequest("zoomPip=" + (tvState.pipZoom ? 0 : 1), new Callable() {
                @Override
                public void call() {
                    tvState.pipZoom = !tvState.pipZoom;
                }
            });
        } else {
            httpRequest("zoomMain=" + (tvState.zoom ? 0 : 1), new Callable() {
                @Override
                public void call() {
                    tvState.zoom = !tvState.zoom;
                }
            });
        }
    }

    public void showPip(final boolean show) {
        httpRequest("showPip=" + (show ? 1 : 0), new Callable() {
            @Override
            public void call() {
                tvState.pip = show;
            }
        });
    }

    public void increaseVolume() {
        final int newVolume = Math.min(tvState.volume + 10, 100);

        httpRequest("volume=" + newVolume, new Callable() {
            @Override
            public void call() {
                tvState.volume = newVolume;
            }
        });
    }

    public void decreaseVolume() {
        final int newVolume = Math.max(tvState.volume - 10, 0);

        httpRequest("volume=" + newVolume, new Callable() {
            @Override
            public void call() {
                tvState.volume = newVolume;
            }
        });
    }

    public void timeShift(final boolean on) {

        boolean isTimeShift = tvState.timeShift;
        if(on && !isTimeShift) {
            //Begin timeshift
            httpRequest("timeShiftPause=", new Callable() {
                @Override
                public void call() {
                    tvState.timeShift = on;
                    tvState.timeShiftStart = System.currentTimeMillis();
                }
            });

        } else if(!on && isTimeShift) {
            //Stop timeshift
            long durationMilliseconds = System.currentTimeMillis() - tvState.timeShiftStart;
            long durationSeconds = durationMilliseconds / 1000;
            httpRequest("timeShiftPlay=" + durationSeconds, new Callable() {
                @Override
                public void call() {
                    tvState.timeShift = on;
                    tvState.timeShiftStart = -1;
                }
            });
        }
    }

    public void debug(final boolean on) {
        httpRequest("debug=" + (tvState.debug ? 0 : 1), new Callable() {
            @Override
            public void call() {
                tvState.debug = on;
            }
        });
    }

    public static class TVState implements Serializable {
        boolean zoom;
        boolean pip;
        boolean pipZoom;
        int volume;
        boolean timeShift;
        long timeShiftStart;
        boolean debug;

        TVState() {
            zoom = false;
            pip = false;
            pipZoom = false;
            volume = 0;
            timeShift = false;
            timeShiftStart = 0L;
            debug = false;
        }

        public boolean isZoom() {
            return zoom;
        }

        public boolean isPip() {
            return pip;
        }

        public boolean isPipZoom() {
            return pipZoom;
        }

        public int getVolume() {
            return volume;
        }

        public boolean isTimeShift() {
            return timeShift;
        }

        public boolean isDebug() {
            return debug;
        }
    }
}
