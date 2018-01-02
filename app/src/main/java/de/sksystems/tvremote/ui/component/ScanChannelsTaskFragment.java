package de.sksystems.tvremote.ui.component;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.sksystems.tvremote.dao.ChannelDao;
import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.entity.Channel;
import de.sksystems.tvremote.http.HttpRequestAsync;
import de.sksystems.tvremote.ui.SettingsActivity;

/**
 * Created by Manuel on 01.01.2018.
 */

public class ScanChannelsTaskFragment extends TaskFragment {

    private HttpRequestScanChannelTask mTask;

    private ProgressBar mProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTask = new HttpRequestScanChannelTask(getContext());
        mTask.execute(new Void[]{});
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        if(parent instanceof TaskCallbacks) {
            mCallbacks = (TaskCallbacks) getParentFragment();
        }
    }

    private class HttpRequestScanChannelTask extends HttpRequestAsync<Void, Integer, List<Channel>> {

        private ChannelDao mDao;

        public HttpRequestScanChannelTask(Context context) {
            super(context);
            mDao = AppDatabase.getDatabase(mContext).channelDao();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        @Override
        protected List<Channel> doInBackground(Void... voids) {
            List<Channel> channels = new ArrayList<>();

            JSONObject result = null;
            try {
                result = http.execute("scanChannels=");
            } catch (IOException | JSONException | IllegalArgumentException ex) {
                mError = ex;
                return null;
            }

            try {
                JSONArray channelArray = result.getJSONArray("channels");

                for (int i = 0; i < channelArray.length(); i++) {
                    JSONObject jChannel = channelArray.getJSONObject(i);

                    Channel channel = new Channel();
                    channel.setFrequency(jChannel.getInt("frequency"));
                    channel.setChannel(jChannel.getString("channel"));
                    channel.setQuality(jChannel.getInt("quality"));
                    channel.setProgram(jChannel.getString("program"));
                    channel.setProvider(jChannel.getString("provider"));

                    channels.add(channel);
                }
            } catch (JSONException ex) {
                mError = ex;
                return null;
            }

            mDao.deleteAll();
            mDao.insertAll(channels);
            return channels;
        }

        @Override
        protected void onPostExecute(List<Channel> channels) {
            super.onPostExecute(channels);

            if(mCallbacks != null) {
                mCallbacks.onPostExecute();
            }

            if (mError != null) {
                evalError();
            } else if (channels != null) {
                Toast.makeText(mContext, channels.size() + " Kanäle gefunden", Toast.LENGTH_SHORT).show();
            } else {
                alert("Ein Fehler ist aufgetreten", "Nullpointer in channelliste");
            }
        }
    }
}
