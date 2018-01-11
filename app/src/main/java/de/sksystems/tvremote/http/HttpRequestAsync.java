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

    public interface BeginListener {
        void onBegin();
    }

    public interface SuccessListener {
        void onSuccess();
    }

    public interface FailureListener {
        void onFailure(Exception e);
    }

    public interface CancelledListener {
        void onCancelled();
    }

    protected HttpRequest http;

    protected Exception mError;

    private BeginListener mBeginListener;
    private SuccessListener mSuccessListener;
    private FailureListener mFailureListener;
    private CancelledListener mCancelledListener;

    public HttpRequestAsync(String ip, int timeout) {
        super();
        http = new HttpRequest(ip, timeout, false);
        mError = null;
    }

    public void setBeginListener(BeginListener mBeginListener) {
        this.mBeginListener = mBeginListener;
    }

    public void removeBeginListener() {
        mBeginListener = null;
    }

    public void setSuccessListener(SuccessListener mSuccessListener) {
        this.mSuccessListener = mSuccessListener;
    }

    public void removeSuccessListener() {
        mSuccessListener = null;
    }

    public void setFailureListener(FailureListener mFailureListener) {
        this.mFailureListener = mFailureListener;
    }

    public void removeFailureListener() {
        mFailureListener = null;
    }

    public void setCancelledListener(CancelledListener mCancelledListener) {
        this.mCancelledListener = mCancelledListener;
    }

    public void removeCancelledListener() {
        mCancelledListener = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(mBeginListener != null) {
            mBeginListener.onBegin();
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        if(mError == null) {
            if(mSuccessListener != null) {
                mSuccessListener.onSuccess();
            }
        } else {
            if(mFailureListener != null) {
                mFailureListener.onFailure(mError);
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        if(mCancelledListener != null) {
            mCancelledListener.onCancelled();
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
