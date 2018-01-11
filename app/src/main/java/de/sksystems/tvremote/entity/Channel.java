package de.sksystems.tvremote.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Manuel on 28.12.2017.
 */

@Entity(tableName = "channel")
public class Channel implements Comparable<Channel> {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "channel")
    private String channel;

    @ColumnInfo(name = "frequency")
    private int frequency;

    @ColumnInfo(name = "quality")
    private int quality;

    @ColumnInfo(name = "program")
    private String program;

    @ColumnInfo(name = "provider")
    private String provider;

    @ColumnInfo(name = "favorite")
    private boolean favorite;

    @ColumnInfo(name = "order")
    private int order;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void toggleFavorite() {
        favorite = !favorite;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return program;
    }

    @Override
    public int compareTo(@NonNull Channel o) {
        return Integer.compare(this.quality, o.getQuality());
    }
}
