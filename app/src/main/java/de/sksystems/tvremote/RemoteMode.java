package de.sksystems.tvremote;

import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public abstract class RemoteMode extends AppCompatActivity {

    protected Remote remote;

    protected List<Channel> channels = new ArrayList<>();

    protected ListView channelList;

    protected ArrayAdapter channelListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFirstRun();

        remote = new Remote(this, "10.0.2.2", 6000);

        setContentView(getActivityId());
        channelList = (ListView) findViewById(R.id.listChannels);

        channelListAdapter = new ArrayAdapter<Channel>(this, R.layout.channel, channels);
        channelList.setAdapter(channelListAdapter);

        channelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                remote.channelMain(channels.get(position));
            }
        });
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
                            channels = remote.scanChannels();
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
        channels.clear();
        channels.addAll(remote.scanChannels());
        channelListAdapter.notifyDataSetChanged();
        return true;
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
