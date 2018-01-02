package de.sksystems.tvremote.http;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Manuel on 01.01.2018.
 */

public class HttpRequestParamTask extends HttpRequestAsync<String, Integer, Void> {

    public HttpRequestParamTask(Context context) {
        super(context);
    }

    @Override
    protected Void doInBackground(String... strings) {
        for (String s : strings) {
            JSONObject result = null;
            try {
                result = http.execute(s);
            } catch (IOException | JSONException | IllegalArgumentException e) {
                mError = e;
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(mError != null) {
            evalError();
        }
    }
}