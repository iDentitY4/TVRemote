package de.sksystems.tvremote.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.sksystems.tvremote.dao.ChannelDao;
import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.entity.Channel;

/**
 * Created by Manuel on 02.01.2018.
 */

public class HttpRequestScanChannelTask extends HttpRequestAsync<Void, Integer, List<Channel>> {

    private ChannelDao mDao;

    public HttpRequestScanChannelTask(AppDatabase db, String ip, int timeout) {
        super(ip, timeout);
        mDao = db.channelDao();
    }

    @Override
    protected List<Channel> doInBackground(Void... voids) {
        List<Channel> channels = new ArrayList<>();

        JSONObject result;
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

        /*if (mError != null) {
            evalError();
        } else if (channels != null) {
            Toast.makeText(mContext, channels.size() + " Kanäle gefunden", Toast.LENGTH_SHORT).show();
        } else {
            alert("Ein Fehler ist aufgetreten", "Nullpointer in channelliste");
        }*/
    }
}
