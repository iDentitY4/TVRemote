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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class RemoteMode extends AppCompatActivity {

    protected HttpRequest httpHandler;

    protected List<Channel> channels = new ArrayList<>();

    protected ListView channelList;

    protected ArrayAdapter channelListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpHandler = new HttpRequest(getResources().getString(R.string.tvip), 6000, true);

        checkFirstRun();

        setContentView(getActivityId());
        channelList = (ListView) findViewById(R.id.listChannels);

        channelListAdapter = new ArrayAdapter<Channel>(this, R.layout.channel, channels);
        channelList.setAdapter(channelListAdapter);

        channelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectChannel(position);
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
                            scanChannels();
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

    protected boolean scanChannels()
    {
        try
        {
            JSONObject scannedChannels = httpHandler.execute(getResources().getString(R.string.scanChannels));

            JSONArray channelArray = scannedChannels.getJSONArray("channels");
            for(int i = 0; i < channelArray.length(); i++)
            {
                JSONObject jChannel = channelArray.getJSONObject(i);
                Channel channel = new Channel();
                channel.frequency = jChannel.getInt("frequency");
                channel.id = jChannel.getString("channel");
                channel.quality = jChannel.getInt("quality");
                channel.program = jChannel.getString("program");
                channel.provider = jChannel.getString("provider");
                channels.add(channel);
            }
            channelListAdapter.notifyDataSetChanged();
        }
        catch(IOException | JSONException | IllegalArgumentException e)
        {
            e.printStackTrace();

            new AlertDialog.Builder(this)
                    .setTitle("Fehler")
                    .setMessage(e.getMessage())
                    .setNeutralButton("OK", null)
                    .show();
            return false;
        }
        return true;
    }

    public boolean onChannelScanMenuItemClicked(MenuItem v)
    {
        return scanChannels();
    }

    public void selectChannel(int position)
    {
        try {
            httpHandler.execute(getResources().getString(R.string.channelMain) + channels.get(position).id);
        }
        catch(IOException | JSONException | IllegalArgumentException e)
        {
            e.printStackTrace();

            new AlertDialog.Builder(this)
                    .setTitle("Fehler")
                    .setMessage(e.getMessage())
                    .setNeutralButton("OK", null)
                    .show();
        }
    }
}
