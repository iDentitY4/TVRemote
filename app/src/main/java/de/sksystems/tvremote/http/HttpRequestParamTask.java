package de.sksystems.tvremote.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Manuel on 01.01.2018.
 */

public class HttpRequestParamTask extends HttpRequestAsync<String, Integer, Void> {

    public HttpRequestParamTask(String ip, int timeout) {
        super(ip, timeout);
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
}