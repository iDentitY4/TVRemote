package de.sksystems.tvremote;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.List;

import de.sksystems.tvremote.dao.ChannelDao;
import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.entity.Channel;

/**
 * Created by Manuel on 29.12.2017.
 */

public class ChannelListViewModel extends AndroidViewModel {

    private final LiveData<List<Channel>> mChannelList;

    private AppDatabase mAppDatabase;

    public ChannelListViewModel(@NonNull Application application) {
        super(application);

        mAppDatabase = AppDatabase.getDatabase(this.getApplication());

        ChannelDao channelDao = mAppDatabase.channelDao();
        if(PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext())
                        .getBoolean("fav_only", false)) {
            mChannelList = channelDao.getAllDistinctMaxQualityFavorites();
        }
        else
        {
            mChannelList = channelDao.getAllDistinctMaxQuality();
        }

    }

    public LiveData<List<Channel>> getChannelList() {
        return mChannelList;
    }

    public LiveData<List<Channel>> getChannelListIgnoreFavMode() {
        return mAppDatabase.channelDao().getAllDistinctMaxQuality();
    }
}
