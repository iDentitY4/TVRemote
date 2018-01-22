package de.sksystems.tvremote.ui;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import de.sksystems.tvremote.SharedPreferencesKeys;
import de.sksystems.tvremote.http.HttpRequest;
import de.sksystems.tvremote.http.HttpRequestAsync;
import de.sksystems.tvremote.R;
import de.sksystems.tvremote.RemoteController;
import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.http.HttpRequestScanChannelTask;
import de.sksystems.tvremote.ui.component.LoadingPreference;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || TVPreferenceFragment.class.getName().equals(fragmentName)
                || ChannelsPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class TVPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_tv);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("tv_ip"));
            bindPreferenceSummaryToValue(findPreference("tv_timeout"));

            SwitchPreference mDebugMode = (SwitchPreference) findPreference("tv_debugmode");
            mDebugMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    RemoteController.getInstance(getContext()).debug((Boolean) o);
                    return true;
                }
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ChannelsPreferenceFragment extends PreferenceFragment {

        private static ChannelsPreferenceFragment active;

        private HttpRequestScanChannelTask runningTask;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_channels);
            setHasOptionsMenu(true);

            active = this;

            final LoadingPreference channelScan = (LoadingPreference) findPreference("scan_channels");
            channelScan.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(runningTask != null) {
                        return false;
                    }
                    else
                    {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                        String ip = pref.getString(SharedPreferencesKeys.TV.IP, null);
                        int timeout = Integer.parseInt(pref.getString(SharedPreferencesKeys.TV.TIMEOUT, "6000"));

                        final ProgressBar channelScanProgress = channelScan.getProgressBar();

                        runningTask = new HttpRequestScanChannelTask(AppDatabase.getDatabase(getContext()), ip, timeout);
                        runningTask.setBeginListener(new HttpRequestAsync.BeginListener() {
                            @Override
                            public void onBegin() {
                                channelScanProgress.setVisibility(View.VISIBLE);
                            }
                        });

                        runningTask.setSuccessListener(new HttpRequestAsync.SuccessListener() {
                            @Override
                            public void onSuccess() {
                                if(active != null) {
                                    runningTask = null;
                                    channelScanProgress.setVisibility(View.GONE);
                                }
                            }
                        });

                        runningTask.setFailureListener(new HttpRequestAsync.FailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                if(active != null) {
                                    runningTask = null;
                                    channelScanProgress.setVisibility(View.GONE);
                                    Toast.makeText(active.getContext().getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        runningTask.setCancelledListener(new HttpRequestAsync.CancelledListener() {
                            @Override
                            public void onCancelled() {
                                if(active != null) {
                                    runningTask = null;
                                    channelScanProgress.setVisibility(View.GONE);
                                    Toast.makeText(active.getContext().getApplicationContext(), "Vorgang abgebrochen", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        runningTask.execute(new Void[]{});
                        return true;
                    }
                }
            });
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            if(runningTask != null) {
                runningTask.cancel(true);
            }

            active = null;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}