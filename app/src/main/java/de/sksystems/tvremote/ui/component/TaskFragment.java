package de.sksystems.tvremote.ui.component;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Manuel on 01.01.2018.
 */

public abstract class TaskFragment extends Fragment {

    public interface TaskCallbacks {
        void onPreExecute();
        void onProgressUpdate();
        void onCancelled();
        void onPostExecute();
    }

    protected TaskCallbacks mCallbacks; 

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
