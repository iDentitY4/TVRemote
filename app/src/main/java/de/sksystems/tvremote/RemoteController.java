package de.sksystems.tvremote;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by Manuel on 07.12.2017.
 */

public class RemoteController {

    public static final int HTTP_REQUEST_TIMEOUT = 6000;

    protected Context context;

    public RemoteController(Context context) {
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

        private ProgressDialog dialog;
        private HttpRequest httpHandler;

        private Exception error = null;

        public HttpRequestAsync() {
            dialog = new ProgressDialog(RemoteController.this.context);

            String ip = RemoteController.this.context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("tv_ip", "10.0.2.2");
            httpHandler = new HttpRequest(ip, HTTP_REQUEST_TIMEOUT, false);
        }

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
                    error = e;
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
                        error = ex;
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

            if(error != null) {
                error.printStackTrace();

                new AlertDialog.Builder(RemoteController.this.context)
                        .setTitle("Ein Fehler ist aufgetreten")
                        .setMessage(error.getMessage())
                        .setNeutralButton("OK", null)
                        .show();
            }
            else {
                Log.i("TVRemote", "Http request finished");

                if (RemoteController.this.context instanceof RemoteModeActivity) {
                    ((RemoteModeActivity) RemoteController.this.context).notifyAdapterDataChanged();
                }
            }
        }
    }
}
