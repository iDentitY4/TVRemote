package de.sksystems.tvremote;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Manuel on 06.12.2017.
 */

public class ChannelListAdapter extends BaseAdapter {
    Context context;
    private List<Channel> channels;
    private LayoutInflater inflater;

    public ChannelListAdapter(Context context, List<Channel> channels) {
        this.context = context;
        this.channels = channels;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Channel getItem(int position) {
        return channels.get(position);
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.channel, null);
        TextView text = (TextView) vi.findViewById(R.id.program);
        text.setText(channels.get(position).program);
        return vi;
    }
}
