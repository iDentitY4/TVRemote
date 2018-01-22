package de.sksystems.tvremote.ui.adapter;

import android.app.Activity;
import android.arch.persistence.room.Update;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.sksystems.tvremote.R;
import de.sksystems.tvremote.dao.ChannelDao;
import de.sksystems.tvremote.dao.UpdateChannelsTask;
import de.sksystems.tvremote.db.AppDatabase;
import de.sksystems.tvremote.entity.Channel;

/**
 * Created by Manuel on 30.12.2017.
 */

public class ChannelFavoritesAdapter extends ArrayAdapter<Channel> {

    protected ChannelDao mChannelDao;

    protected Activity mContext;
    protected LayoutInflater inflater;

    protected boolean movingActive;
    protected int positionFrom;
    protected final int positionNone = -1;

    public ChannelFavoritesAdapter(Activity context, List<Channel> items) {
        super(context, R.layout.sortable_channel, R.id.textViewProgram, items);
        mContext = context;
        inflater = context.getLayoutInflater();
        movingActive = false;
        mChannelDao = AppDatabase.getDatabase(mContext).channelDao();
    }

    static class ViewHolder {
        public TextView textViewProgram;        // final
        public ImageView imageViewFavorite;     // final
        public ImageView imageViewMove;         // final
        public Channel channel;                 // variable
    }

    private void setUiListElement(ViewHolder holder) {
        holder.textViewProgram.setText(holder.channel.getProgram());
        holder.imageViewFavorite.setImageResource(holder.channel.isFavorite()
                ? R.drawable.ic_star_accent_24dp
                : R.drawable.ic_star_border_accent_24dp);

        int res = 0;
        if(!movingActive || (holder.channel.getOrder() == positionFrom)) {
            res = R.drawable.ic_import_export_accent_24dp;
        }
        else if(holder.channel.getOrder() < positionFrom) {
            res = R.drawable.ic_reply_accent_24dp;
            holder.imageViewMove.setScaleY(1);
        }
        else {
            res = R.drawable.ic_reply_accent_24dp;
            holder.imageViewMove.setScaleY(-1);
        }
        holder.imageViewMove.setImageResource(res);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // no convertView available; create a new one
            convertView = inflater.inflate(R.layout.sortable_channel, parent, false);
            holder = new ViewHolder();
            holder.textViewProgram = (TextView)convertView.findViewById(R.id.textViewProgram);
            holder.imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageViewFavorite);
            holder.imageViewMove = (ImageView)convertView.findViewById(R.id.imageViewMove);
            holder.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder holder = getViewHolder(v);
                    holder.channel.toggleFavorite();
                    setUiListElement(holder);
                    v.invalidate();
                    new UpdateChannelsTask(getContext()).execute(holder.channel);
                }
            });
            holder.imageViewMove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!movingActive)
                        beginMoving(getViewHolder(v));
                    else
                        endMoving(getViewHolder(v));
                }
            });
            holder.channel = getItem(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (movingActive)
                        endMoving((ViewHolder)v.getTag());
                }
            });
            convertView.setTag(holder);
        }
        else {
            // convertView comes from recycling of views being scrolled out of sight
            holder = (ViewHolder)convertView.getTag();
        }
        //holder.channel.setOrder(position);
        holder.channel = getItem(position);
        setUiListElement(holder);
        return convertView;
    }

    protected ViewHolder getViewHolder(View v) {
        RelativeLayout listElement = (RelativeLayout)v.getParent();
        return (ViewHolder)listElement.getTag();
    }

    protected void beginMoving(ViewHolder holder) {
        positionFrom = getPosition(holder.channel);
        movingActive = true;
        notifyDataSetChanged();     // triggers refresh of the ListView
        Toast.makeText(mContext, "Wähle das Ziel", Toast.LENGTH_SHORT).show();
    }

    protected void endMoving(ViewHolder holder) {
        int positionTo = getPosition(holder.channel);

        Channel channel = getItem(positionFrom);
        if (positionFrom > positionTo) {
            // move channel up
            for (int i = positionFrom; i >= positionTo + 1; i--) {
                setItem(getItem(i - 1), i);
            }
        }
        else if (positionFrom < positionTo)  {
            // move channel down f
            for (int i = positionFrom; i <= positionTo - 1; i++) {
                setItem(getItem(i + 1), i);
            }
        }
        // else positionFrom == positionTo: do nothing

        setItem(channel, positionTo);

        positionFrom = positionNone;
        movingActive = false;


        Channel[] channels = new Channel[getCount()];
        for(int i = 0; i < getCount(); i++) {
            channels[i] = getItem(i);
            channels[i].setOrder(i);
        }
        notifyDataSetChanged();     // triggers refresh of the ListView

        new UpdateChannelsTask(getContext()).execute(channels);
    }

    protected void setItem(Channel channel, int position) {
        // setItem is unfortunately missing in class ArrayAdapter
        remove(getItem(position));
        insert(channel, position);
    }
}
