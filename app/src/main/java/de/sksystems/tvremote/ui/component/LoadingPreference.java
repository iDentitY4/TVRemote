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

public class LoadingPreference extends Preference {

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

    public ProgressBar getProgressBar() {
        return mProgress;
    }
}
