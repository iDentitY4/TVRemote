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
        void onSuccess();
        void onFailure(Exception e);
    }

    protected HttpRequest http;

    protected Exception mError;

    private List<RequestListener> mListeners;

    public HttpRequestAsync(String ip, int timeout) {
        super();
        http = new HttpRequest(ip, timeout, false);
        mError = null;
        mListeners = new ArrayList<>();
    }

    public void addRequestListener(RequestListener listener) {
        mListeners.add(listener);
    }

    public void removeRequestListener(RequestListener listener) {
        mListeners.remove(listener);
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

        if(mError == null) {
            for(RequestListener l : mListeners) {
                l.onSuccess();
            }
        } else {
            for(RequestListener l : mListeners) {
                l.onFailure(mError);
            }
        }
    }

    /*public void evalError() {
        if(mError instanceof IOException) {
            alert("Verbindung zum Fernseher konnte nicht hergestellt werden", mError.getMessage());
        }
        else
        {
            alert("Ein Fehler ist aufgetreten", mError.getMessage());
        }
    }*/

    /*protected void alert(String title, String message) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("OK", null)
                .show();
    }*/
}
