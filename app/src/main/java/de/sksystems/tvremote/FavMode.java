package de.sksystems.tvremote;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Manuel on 06.12.2017.
 */

public class FavMode extends RemoteMode {

    @Override
    protected int getActivityId() {
        return R.layout.activity_fav_mode;
    }


}
