package de.sksystems.tvremote.ui.component;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import de.sksystems.tvremote.R;
import de.sksystems.tvremote.entity.Channel;

/**
 * Created by Manuel on 30.12.2017.
 */

public class LoadingPreference extends Preference implements TaskFragment.TaskCallbacks{

    private ProgressBar mProgress;

    public LoadingPreference(Context context) {
        this(context, null);
    }

    public LoadingPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.pref_loading);
    }

    public LoadingPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.pref_loading);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);

        mProgress = view.findViewById(R.id.progress);
        mProgress.setIndeterminate(true);
        return view;
    }

    @Override
    public void onPreExecute() {
        if(mProgress != null) {
            mProgress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProgressUpdate() {

    }

    @Override
    public void onCancelled() {
        if(mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPostExecute() {
        if(mProgress != null) {
            mProgress.setVisibility(View.GONE);
        }
    }
}
