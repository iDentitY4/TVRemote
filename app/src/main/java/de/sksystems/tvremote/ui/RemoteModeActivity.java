package de.sksystems.tvremote.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import java.util.List;

import de.sksystems.tvremote.ChannelListViewModel;
import de.sksystems.tvremote.SharedPreferencesKeys;
import de.sksystems.tvremote.ui.adapter.ChannelRecyclerAdapter;
import de.sksystems.tvremote.R;
import de.sksystems.tvremote.RemoteController;
import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.entity.Channel;
import de.sksystems.tvremote.ui.component.IpAddressDialogFragment;
import de.sksystems.tvremote.util.FragmentClickHandler;

public abstract class RemoteModeActivity extends AppCompatActivity {

    SharedPreferences mPreferences;

    protected RemoteController mRemoteControl;

    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    protected ChannelListViewModel mChannelListViewModel;
    protected final Observer<List<Channel>> mViewModelObserver = new Observer<List<Channel>>() {
        @Override
        public void onChanged(@Nullable final List<Channel> channels) {
            mAdapter = new ChannelRecyclerAdapter(channels);
            mRecyclerView.setAdapter(mAdapter);
        }
    };

    FragmentClickHandler mControlBarClickHandler;
    FragmentClickHandler mIpAddressDialogClickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getActivityId());

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(mPreferences.getString(SharedPreferencesKeys.TV.IP, null) == null) {
            IpAddressDialogFragment dialog = new IpAddressDialogFragment();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(android.R.id.content, dialog).addToBackStack(null).commit();

            mIpAddressDialogClickHandler = dialog;
        }

        mRecyclerView = findViewById(R.id.channel_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                ((LinearLayoutManager)mLayoutManager).getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mChannelListViewModel = ViewModelProviders.of(this).get(ChannelListViewModel.class);
        mChannelListViewModel.getChannelList().observe(this, mViewModelObserver);

        mRemoteControl = RemoteController.getInstance(this);
        if(mRemoteControl.getRunningTask() != null) {
            mRemoteControl.getRunningTask().attach(this);
        }

        mControlBarClickHandler = (ControlBarFragment) getFragmentManager().findFragmentById(R.id.control_bar_easy);

        LinearLayout easy_toolbar = findViewById(R.id.control_bar_easy);
        ToggleButton btnTimeshift = easy_toolbar.findViewById(R.id.control_timeshift);
        btnTimeshift.setChecked(mRemoteControl.getTVState().isTimeShift());
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRemoteControl.storeTVState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mChannelListViewModel.getChannelList().observe(this, mViewModelObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected abstract @LayoutRes int getActivityId();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.easymode_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onControlBarItemClick(View v) {
        mControlBarClickHandler.onClick(v);
    }

    public void onIpAddressDialogFragmentClick(View v) {
        if(mIpAddressDialogClickHandler != null) {
            mIpAddressDialogClickHandler.onClick(v);
        }
    }

    public static class ControlBarFragment extends Fragment implements FragmentClickHandler {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.controlbar_easy, container, false);
        }

        public void onClick(View v) {

            RemoteController rc = RemoteController.getInstance(getContext());

            switch(v.getId()) {
                case R.id.control_vol_inc: {
                    rc.increaseVolume();
                    break;
                }
                case R.id.control_vol_dec: {
                    rc.decreaseVolume();
                    break;
                }
                case R.id.control_timeshift: {
                    rc.timeShift(((ToggleButton) v).isChecked());
                    break;
                }
            }
        }
    }
}
