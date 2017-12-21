package de.sksystems.tvremote;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manuel on 17.12.2017.
 */

public class TVDataModel implements Serializable {

    private static TVDataModel instance;

    public static TVDataModel getInstance() {
        if(instance == null) {
            instance = new TVDataModel();
        }

        return instance;
    }

    private static final String STATUS_FILENAME = "tvdata";

    private List<Channel> channels;

    private boolean zoom;

    private boolean pip;

    private boolean pipZoom;

    private int volume;

    private boolean timeShift;

    private TVDataModel() {
        channels = new ArrayList<>();
        zoom = false;
        pip = false;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public Channel getChannel(int position) {
        return channels.get(position);
    }

    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }

    public int increaseVolume()
    {
        if(volume >= 100)
        {
            volume = 100;
            return volume;
        }
        else
        {
            volume += 10;
            return volume;
        }
    }

    public int decreaseVolume() {
        if(volume <= 0)
        {
            volume = 0;
            return volume;
        }
        else
        {
            volume -= 10;
            return volume;
        }
    }

    public boolean isZoom() {
        return zoom;
    }

    public void setZoom(boolean zoom) {
        this.zoom = zoom;
    }

    public boolean isPip() {
        return pip;
    }

    public void setPip(boolean pip) {
        this.pip = pip;
    }

    public boolean isPipZoom() {
        return pipZoom;
    }

    public void setPipZoom(boolean pipZoom) {
        this.pipZoom = pipZoom;
    }

    public boolean isTimeShift() {
        return timeShift;
    }

    public void setTimeShift(boolean timeShift) {
        this.timeShift = timeShift;
    }

    public static void save(Context context) {


        try {
            FileOutputStream fos = context.openFileOutput(STATUS_FILENAME, Context.MODE_PRIVATE);

            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Log.i("TVRemote", "Persisting tv data");
            oos.writeObject(instance);
            oos.flush();
            oos.close();
            fos.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void load(Context context) {

        try {
            FileInputStream fis = context.openFileInput(STATUS_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Log.i("TVRemote", "Loading tv data");
            instance = (TVDataModel) ois.readObject();
            ois.close();
            fis.close();
        }
        catch(IOException | ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
}
