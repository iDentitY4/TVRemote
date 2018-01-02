package de.sksystems.tvremote.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import de.sksystems.tvremote.R;
import de.sksystems.tvremote.RemoteController;
import de.sksystems.tvremote.entity.Channel;

/**
 * Created by Manuel on 29.12.2017.
 */

public class ChannelRecyclerAdapter extends RecyclerView.Adapter<ChannelRecyclerAdapter.ChannelViewHolder>{

    private List<Channel> mDataset;

    public ChannelRecyclerAdapter(List<Channel> channels) {
        mDataset = channels;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel, parent, false);

        ChannelViewHolder vh = new ChannelViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder holder, int position) {

        final Channel c = mDataset.get(position);

        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteController.getInstance(v.getContext()).selectChannel(c);
            }
        });

        holder.mTextView.setText(c.getProgram());
        if(c.isFavorite()) {
            holder.mFavorite.setVisibility(View.VISIBLE);
        } else {
            holder.mFavorite.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ChannelViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout mRoot;

        public TextView mTextView;

        public ImageView mFavorite;

        public ChannelViewHolder(View v) {
            super(v);

            mRoot = (RelativeLayout) v.getRootView();
            mTextView = v.findViewById(R.id.program);
            mFavorite = v.findViewById(R.id.imageViewFavorite);
        }
    }
}
