package de.sksystems.tvremote.ui.component;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import de.sksystems.tvremote.R;
import de.sksystems.tvremote.SharedPreferencesKeys;
import de.sksystems.tvremote.ui.RemoteModeActivity;
import de.sksystems.tvremote.util.FragmentClickHandler;

/**
 * Created by Manuel on 02.01.2018.
 */

public class IpAddressDialogFragment extends DialogFragment implements FragmentClickHandler {

    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private EditText mEditIp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((RemoteModeActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((RemoteModeActivity) getActivity()).getSupportActionBar().show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_ipaddress, container, false);
        mEditIp = v.findViewById(R.id.edit_ip);
        return v;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.confirm_ip: {
                if(mEditIp != null) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String ip = mEditIp.getText().toString();
                    if(isValidIp(ip)) {
                        pref.edit().putString(SharedPreferencesKeys.TV.IP, mEditIp.getText().toString()).commit();
                        getActivity().getFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Ungültiges format!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    private boolean isValidIp(String value) {
        return PATTERN.matcher(value).matches();
    }
}
