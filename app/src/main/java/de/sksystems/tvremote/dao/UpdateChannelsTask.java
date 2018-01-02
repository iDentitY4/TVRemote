package de.sksystems.tvremote.dao;

import android.content.Context;
import android.os.AsyncTask;

import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.entity.Channel;

/**
 * Created by Manuel on 31.12.2017.
 */

public class UpdateChannelsTask extends AsyncTask<Channel, Void, Void> {

    private ChannelDao mChannelDao;

    public UpdateChannelsTask(Context context) {
        mChannelDao = AppDatabase.getDatabase(context).channelDao();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Channel... channels) {
        mChannelDao.updateChannels(channels);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
