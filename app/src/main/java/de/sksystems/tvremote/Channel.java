package de.sksystems.tvremote;

import java.io.Serializable;

/**
 * Created by Manuel on 29.11.2017.
 */

public class Channel implements Serializable{
    public int frequency;
    public String id;
    public int quality;
    public String program;
    public String provider;

    public Channel()
    {

    }

    @Override
    public String toString() {
        return program;
    }
}
