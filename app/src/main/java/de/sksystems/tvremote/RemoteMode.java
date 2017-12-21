package de.sksystems.tvremote;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public abstract class RemoteMode extends AppCompatActivity {

    protected Remote remote;

    protected ListView channelList;

    protected ArrayAdapter<Channel> channelListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFirstRun();

        remote = new Remote(this);

        setContentView(getActivityId());
        channelList = (ListView) findViewById(R.id.listChannels);

        channelListAdapter = new ArrayAdapter<>(this, R.layout.channel, remote.getChannels());
        channelList.setAdapter(channelListAdapter);

        channelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                remote.selectChannel(position);
            }
        });

        LinearLayout easy_toolbar = (LinearLayout)findViewById(R.id.easy_toolbar);
        ((ToggleButton) easy_toolbar.findViewById(R.id.btnTimeShift)).setChecked(TVDataModel.getInstance().isPip());
    }

    @Override
    protected void onPause() {
        super.onPause();

        TVDataModel.save(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        TVDataModel.load(getApplicationContext());
        channelListAdapter = new ArrayAdapter<Channel>(this, R.layout.channel, remote.getChannels());
        channelList.setAdapter(channelListAdapter);
        channelListAdapter.notifyDataSetChanged();
    }

    public void notifyAdapterDataChanged()
    {
        this.channelListAdapter.notifyDataSetChanged();
    }

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {

            //Run sendersuchlauf
            new AlertDialog.Builder(this)
                    .setTitle(R.string.titleChannelSearchDlg)
                    .setMessage(R.string.msgChannelSearchDlg)
                    .setNeutralButton("Suchlauf starten", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            remote.scanChannels();
                            channelListAdapter.notifyDataSetChanged();
                        }
                    })
                    .show();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    protected abstract @LayoutRes int getActivityId();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.easymode_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onChannelScanMenuItemClicked(MenuItem v)
    {
        remote.scanChannels();
        channelListAdapter.notifyDataSetChanged();
        return true;
    }

    public void onBtnVolIncClicked(View v)
    {
        remote.increaseVolume();
    }

    public void onBtnVolDecClicked(View v)
    {
        remote.decreaseVolume();
    }

    public void aspectRatioBtnClicked(View view)
    {
        remote.zoomMain();
    }

    public void pipBtnClicked(View v)
    {
        if(v instanceof ToggleButton) {
            remote.showPip(((ToggleButton) v).isChecked());
        }
    }
}
