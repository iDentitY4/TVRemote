package de.sksystems.tvremote;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class EasyMode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_mode);
    }

    public void fillChannels(List<Channel> channels) {
        //R.id.listChannels
        View channelsView = findViewById(R.id.listChannels);
        if(channelsView instanceof ListView) {
            for(Channel c : channels) {
                View channelView = null;//TODO
                ((ListView) channelsView).addView(channelView);
            }
        }
    }

    public void onVolIncClicked(View v) {

    }

    public void onVolDecClicked(View v) {

    }
}
