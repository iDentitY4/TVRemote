package de.sksystems.tvremote;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manuel on 07.12.2017.
 */

public class Remote {

    protected Context context;

    protected HttpRequest httpHandler;

    private boolean isZoom = false;

    public Remote(Context context, String ip, int timeout)
    {
        this.context = context;
        this.httpHandler = new HttpRequest(ip, timeout, true);
    }

    private JSONObject runRequest(String request)
    {
        try
        {
            return httpHandler.execute(request);
        }
        catch(IOException | JSONException | IllegalArgumentException e)
        {
            e.printStackTrace();

            new AlertDialog.Builder(context)
                    .setTitle("Fehler")
                    .setMessage(e.getMessage())
                    .setNeutralButton("OK", null)
                    .show();
        }
        return null;
    }

    public List<Channel> scanChannels()
    {
        List<Channel> channels = new ArrayList<>();
        JSONObject scanChannelResult = runRequest("scanChannels=");
        if(scanChannelResult != null)
        {
            try {
                JSONArray channelArray = scanChannelResult.getJSONArray("channels");

                for (int i = 0; i < channelArray.length(); i++) {
                    JSONObject jChannel = channelArray.getJSONObject(i);
                    Channel channel = new Channel();
                    channel.frequency = jChannel.getInt("frequency");
                    channel.id = jChannel.getString("channel");
                    channel.quality = jChannel.getInt("quality");
                    channel.program = jChannel.getString("program");
                    channel.provider = jChannel.getString("provider");
                    channels.add(channel);
                }
            }
            catch(JSONException ex)
            {
                ex.printStackTrace();
            }
        }

        return channels;
    }

    public void channelMain(Channel channel)
    {
        runRequest("channelMain=" + channel.id);
    }

    public void zoomMain()
    {
        isZoom = !isZoom;
        runRequest("zoomMain=" + (isZoom ? 1 : 0));
    }

    public void showPip(boolean show)
    {
        runRequest("showPip=" + (show ? 1 : 0));
    }
}
