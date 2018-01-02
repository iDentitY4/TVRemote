package de.sksystems.tvremote.ui.component;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.sksystems.tvremote.R;

/**
 * Created by Manuel on 30.12.2017.
 */

public class ProgressBarFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progressbar,container, false);
    }
}
