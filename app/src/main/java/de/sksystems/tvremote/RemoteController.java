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

import de.sksystems.tvremote.entity.Channel;
import de.sksystems.tvremote.dao.ChannelDao;
import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.http.HttpRequestAsync;
import de.sksystems.tvremote.http.HttpRequestParamTask;

/**
 * Created by Manuel on 07.12.2017.
 */

public class RemoteController {

    private static final String TV_STATE_FILENAME = "TV_STATE";

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

    private void httpRequest(String request) {
        if(mRunningTask == null) {
            String ip = mPreferences.getString(SharedPreferencesKeys.TV.IP, null);
            int timeout = Integer.parseInt(mPreferences.getString(SharedPreferencesKeys.TV.TIMEOUT, "6000"));

            mRunningTask = new HttpRequestParamTask(ip, timeout);
            mRunningTask.addRequestListener(new HttpRequestAsync.RequestListener() {
                @Override
                public void onBegin() {

                }

                @Override
                public void onSuccess() {
                    mRunningTask = null;
                }

                @Override
                public void onFailure(Exception e) {
                    mRunningTask = null;
                    Toast.makeText(mContext, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
            mRunningTask.execute(new String[]{request});
        }
        else
        {
            Toast.makeText(mContext, "Bitte warten...", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectChannel(Channel channel) {
        if (tvState.pip) {
            httpRequest("channelPip=" + channel.getChannel());
        } else {
            httpRequest("channelMain=" + channel.getChannel());
        }
    }

    public void zoomMain() {
        if (tvState.pip) {
            httpRequest("zoomPip=" + (tvState.pip ? 1 : 0));
            tvState.pipZoom = !tvState.pipZoom;
        } else {
            httpRequest("zoomMain=" + (tvState.zoom ? 1 : 0));
            tvState.zoom = !tvState.zoom;
        }
    }

    public void showPip(boolean show) {
        tvState.pip = show;
        httpRequest("showPip=" + (show ? 1 : 0));
    }

    public void increaseVolume() {
        tvState.volume += 10;

        if(tvState.volume >= 100) {
            tvState.volume = 100;
        }

        httpRequest("volume=" + tvState.volume);
    }

    public void decreaseVolume() {
        tvState.volume -= 10;

        if(tvState.volume < 0) {
            tvState.volume = 0;
        }

        httpRequest("volume=" + tvState.volume);
    }

    public void timeShift(boolean on) {
        tvState.timeShift = on;
        if(on) {
            httpRequest("timeShiftPause=");
        }
        else
        {
            httpRequest("timeShiftPlay=1");
        }
    }

    public void debug(boolean on) {
        tvState.debug = on;
        httpRequest("debug=" + (tvState.debug ? 1 : 0));
    }

    public static class TVState implements Serializable {
        boolean zoom;
        boolean pip;
        boolean pipZoom;
        int volume;
        boolean timeShift;
        boolean debug;

        TVState() {
            zoom = false;
            pip = false;
            pipZoom = false;
            volume = 0;
            timeShift = false;
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
