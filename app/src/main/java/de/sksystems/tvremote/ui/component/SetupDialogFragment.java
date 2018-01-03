package de.sksystems.tvremote.ui.component;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.regex.Pattern;

import de.sksystems.tvremote.R;
import de.sksystems.tvremote.SharedPreferencesKeys;
import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.http.HttpRequestAsync;
import de.sksystems.tvremote.http.HttpRequestScanChannelTask;
import de.sksystems.tvremote.ui.RemoteModeActivity;
import de.sksystems.tvremote.util.FragmentClickHandler;

/**
 * Created by Manuel on 02.01.2018.
 */

public class SetupDialogFragment extends DialogFragment implements FragmentClickHandler {

    public interface SetupCompletedListener {
        void onSetupCompleted();
    }

    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private SharedPreferences mPreferences;

    private EditText mEditIp;
    private Button mBtnChannelScan;
    private ProgressBar mProgressChannelScan;

    private SetupCompletedListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SetupCompletedListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SetupCompletedListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_setup, container, false);
        mEditIp = v.findViewById(R.id.setup_edit_ip);
        mBtnChannelScan = v.findViewById(R.id.setup_button_channelscan);
        mProgressChannelScan = v.findViewById(R.id.setup_progress_channelscan);
        return v;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.setup_button_confirm_ip: {
                if(mEditIp != null) {
                    String ip = mEditIp.getText().toString();
                    if(isValidIp(ip)) {
                        mPreferences.edit().putString(SharedPreferencesKeys.TV.IP, mEditIp.getText().toString()).commit();
                        mBtnChannelScan.setEnabled(true);
                    } else {
                        Toast.makeText(getContext(), "Ungültiges format!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case R.id.setup_button_channelscan: {
                String ip = mPreferences.getString(SharedPreferencesKeys.TV.IP, null);
                int timeout = Integer.parseInt(mPreferences.getString(SharedPreferencesKeys.TV.TIMEOUT, "6000"));

                HttpRequestScanChannelTask task = new HttpRequestScanChannelTask(AppDatabase.getDatabase(getContext()), ip, timeout);
                task.addRequestListener(new HttpRequestAsync.RequestListener() {
                    @Override
                    public void onBegin() {
                        mBtnChannelScan.setEnabled(false);
                        mProgressChannelScan.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess() {
                        mListener.onSetupCompleted();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        mProgressChannelScan.setVisibility(View.GONE);
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        mBtnChannelScan.setEnabled(true);
                    }
                });
                task.execute(new Void[]{});
            }
        }
    }

    private boolean isValidIp(String value) {
        return PATTERN.matcher(value).matches();
    }
}
