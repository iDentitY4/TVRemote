package de.sksystems.tvremote.ui.component;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.sksystems.tvremote.dao.ChannelDao;
import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.entity.Channel;
import de.sksystems.tvremote.http.HttpRequestAsync;
import de.sksystems.tvremote.http.HttpRequestScanChannelTask;
import de.sksystems.tvremote.ui.SettingsActivity;

/**
 * Created by Manuel on 01.01.2018.
 */

public class ScanChannelsTaskFragment extends TaskFragment {

    private HttpRequestScanChannelTask mTask;

    private ProgressBar mProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTask = new HttpRequestScanChannelTask(null, "", 0);
        mTask.execute(new Void[]{});
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
