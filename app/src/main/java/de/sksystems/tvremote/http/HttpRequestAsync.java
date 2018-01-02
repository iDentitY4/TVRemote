package de.sksystems.tvremote.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.sksystems.tvremote.SharedPreferencesKeys;
import de.sksystems.tvremote.http.HttpRequest;

/**
 * Created by Manuel on 30.12.2017.
 */

public abstract class HttpRequestAsync<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public interface RequestListener {
        void onBegin();
        void onEnd();
    }

    protected Context mContext;

    protected HttpRequest http;

    protected Exception mError;

    private List<RequestListener> mListeners;

    public HttpRequestAsync(Context context) {
        super();
        mContext = context;
        http = new HttpRequest(getIp(), getTimeout(), false);
        mError = null;
        mListeners = new ArrayList<>();
    }

    public void attach(Context context) {
        mContext = context;
    }

    public void detach() {
        mContext = null;
    }

    public void addRequestListener(RequestListener listener) {
        mListeners.add(listener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        for(RequestListener l : mListeners) {
            l.onBegin();
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        for(RequestListener l : mListeners) {
            l.onEnd();
        }
    }

    public void evalError() {
        if(mError instanceof IOException) {
            alert("Verbindung zum Fernseher konnte nicht hergestellt werden", mError.getMessage());
        }
        else
        {
            alert("Ein Fehler ist aufgetreten", mError.getMessage());
        }
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public String getIp() {
        return getSharedPreferences().getString(SharedPreferencesKeys.TV.IP,null);
    }

    public int getTimeout() {
        return Integer.parseInt(getSharedPreferences().getString(SharedPreferencesKeys.TV.TIMEOUT, "6000"));
    }

    protected void alert(String title, String message) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("OK", null)
                .show();
    }
}
