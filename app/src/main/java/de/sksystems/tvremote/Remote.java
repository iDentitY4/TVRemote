package de.sksystems.tvremote;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ListAdapter;

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

    public Remote(Context context) {
        this.context = context;
    }

    private void httpRequest(String request) {
        new HttpRequestAsync().execute(request);
    }

    public void scanChannels() {
        httpRequest("scanChannels=");
    }

    public List<Channel> getChannels() {
        return TVDataModel.getInstance().getChannels();
    }

    public void selectChannel(int position) {
        if (TVDataModel.getInstance().isPip()) {
            httpRequest("channelPip=" + TVDataModel.getInstance().getChannel(position).id);
        } else {
            httpRequest("channelMain=" + TVDataModel.getInstance().getChannel(position).id);
        }
    }

    public void zoomMain() {
        if (TVDataModel.getInstance().isPip()) {
            httpRequest("zoomPip=" + (TVDataModel.getInstance().isPipZoom() ? 1 : 0));
            TVDataModel.getInstance().setPipZoom(!TVDataModel.getInstance().isPipZoom());
        } else {
            httpRequest("zoomMain=" + (TVDataModel.getInstance().isZoom() ? 1 : 0));
            TVDataModel.getInstance().setZoom(!TVDataModel.getInstance().isZoom());
        }
    }

    public void showPip(boolean show) {
        TVDataModel.getInstance().setPip(show);
        httpRequest("showPip=" + (show ? 1 : 0));
    }

    public void increaseVolume() {
        httpRequest("volume=" + TVDataModel.getInstance().increaseVolume());
    }

    public void decreaseVolume() {
        httpRequest("volume=" + TVDataModel.getInstance().decreaseVolume());
    }

    public void timeShift(boolean on) {
        TVDataModel.getInstance().setTimeShift(on);
        if(on) {
            httpRequest("timeShiftPause=");
        }
        else
        {
            httpRequest("timeShiftPlay=1");
        }
    }

    private class HttpRequestAsync extends AsyncTask<String, Integer, Void> {

        private ProgressDialog dialog = new ProgressDialog(Remote.this.context);

        private HttpRequest httpHandler = new HttpRequest("10.0.2.2", 6000, true);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Wird geladen...");
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            for (String s : strings) {
                JSONObject result = null;
                try {
                    result = httpHandler.execute(s);
                } catch (IOException | JSONException | IllegalArgumentException e) {
                    e.printStackTrace();

                /*new AlertDialog.Builder(Remote.this.context)
                    .setTitle("Fehler")
                    .setMessage(e.getMessage())
                    .setNeutralButton("OK", null)
                    .show();*/
                }

                if (s.startsWith("scanChannels=") && result != null) {
                    try {
                        JSONArray channelArray = result.getJSONArray("channels");

                        for (int i = 0; i < channelArray.length(); i++) {
                            JSONObject jChannel = channelArray.getJSONObject(i);
                            Channel channel = new Channel();
                            channel.frequency = jChannel.getInt("frequency");
                            channel.id = jChannel.getString("channel");
                            channel.quality = jChannel.getInt("quality");
                            channel.program = jChannel.getString("program");
                            channel.provider = jChannel.getString("provider");
                            TVDataModel.getInstance().addChannel(channel);
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();

                        /*new AlertDialog.Builder(Remote.this.context)
                                .setTitle("Fehler")
                                .setMessage(ex.getMessage())
                                .setNeutralButton("OK", null)
                                .show();*/
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(dialog.isShowing()) {
                dialog.dismiss();
            }

            Log.i("TVRemote", "Http request finished");
            if(Remote.this.context instanceof RemoteMode) {
                ((RemoteMode) Remote.this.context).notifyAdapterDataChanged();
            }
        }
    }
}
