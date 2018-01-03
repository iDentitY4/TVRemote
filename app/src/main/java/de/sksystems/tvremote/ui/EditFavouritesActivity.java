package de.sksystems.tvremote.ui;

import de.sksystems.tvremote.ChannelListViewModel;
import de.sksystems.tvremote.R;
import de.sksystems.tvremote.entity.Channel;
import de.sksystems.tvremote.ui.adapter.ChannelFavoritesAdapter;
import de.sksystems.tvremote.ui.adapter.ChannelRecyclerAdapter;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class EditFavouritesActivity extends AppCompatActivity {

    protected ListView mFavlist;
    protected ChannelFavoritesAdapter mListAdapter;
    protected ChannelListViewModel mChannelListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_favourites);
        setupActionBar();

        mFavlist = findViewById(R.id.channel_fav_list);

        mChannelListViewModel = ViewModelProviders.of(this).get(ChannelListViewModel.class);
        final Observer<List<Channel>> channelObserver = new Observer<List<Channel>>() {
            @Override
            public void onChanged(@Nullable final List<Channel> channels) {
                mListAdapter = new ChannelFavoritesAdapter(EditFavouritesActivity.this, channels);
                mFavlist.setAdapter(mListAdapter);
                //Toast.makeText(EditFavouritesActivity.this, "Loaded " + channels.size() + " channels", Toast.LENGTH_SHORT).show();
            }
        };

        mChannelListViewModel.getChannelListIgnoreFavMode().observe(this,channelObserver);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
}
