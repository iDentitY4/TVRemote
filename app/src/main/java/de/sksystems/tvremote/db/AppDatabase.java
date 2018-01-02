package de.sksystems.tvremote.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import de.sksystems.tvremote.dao.ChannelDao;
import de.sksystems.tvremote.entity.Channel;

/**
 * Created by Manuel on 28.12.2017.
 */

@Database(entities= {Channel.class }, version=1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ChannelDao channelDao();

    public static AppDatabase getDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "tvremote-db").build();
    }
}
