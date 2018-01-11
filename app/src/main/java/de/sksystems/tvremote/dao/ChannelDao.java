package de.sksystems.tvremote.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import de.sksystems.tvremote.entity.Channel;

/**
 * Created by Manuel on 28.12.2017.
 */

@Dao
public interface ChannelDao {

    @Query("SELECT * FROM channel")
    LiveData<List<Channel>> getAll();

    @Query("SELECT c.*" +
            " FROM channel c" +
            " INNER JOIN" +
            " (SELECT program, MAX(quality) AS maxQuality" +
            " FROM channel" +
            " GROUP BY program) cGroup" +
            " ON c.program = cGRoup.program" +
            " AND c.quality = cGroup.maxQuality" +
            " ORDER BY c.`order` ASC")
    LiveData<List<Channel>> getAllDistinctMaxQuality();

    @Query("SELECT c.*" +
            " FROM channel c" +
            " INNER JOIN" +
            " (SELECT program, MAX(quality) AS maxQuality" +
            " FROM channel" +
            " GROUP BY program) cGroup" +
            " ON c.program = cGRoup.program" +
            " AND c.quality = cGroup.maxQuality" +
            " AND c.favorite = 1" +
            " ORDER BY c.`order` ASC")
    LiveData<List<Channel>> getAllDistinctMaxQualityFavorites();

    @Query("SELECT * FROM channel WHERE uid = :uid")
    Channel getForId(int uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Channel... channels);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Iterable<Channel> channels);

    @Update
    void updateChannels(Channel... channels);

    @Delete
    void delete(Channel... channels);

    @Query("DELETE FROM channel")
    void deleteAll();
}


