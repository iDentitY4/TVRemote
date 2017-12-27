package de.sksystems.tvremote;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Manuel on 06.12.2017.
 */

public class EasyModeActivity extends RemoteModeActivity {

    @Override
    protected int getActivityId() {
        return R.layout.activity_easy_mode;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.easymode_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menuItemExpertMode:
            {
                Intent intent = new Intent(this, ExpertModeActivity.class);
                startActivity(intent);
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
