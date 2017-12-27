package de.sksystems.tvremote;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ToggleButton;

public class ExpertModeActivity extends RemoteModeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ToggleButton) findViewById(R.id.btnTimeShift)).setChecked(TVDataModel.getInstance().isTimeShift());
    }

    @Override
    protected int getActivityId() {
        return R.layout.activity_expert_mode;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.expertmode_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menuItemEasyMode:
            {
                Intent intent = new Intent(this, EasyModeActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.menuItemSettings:
            {
                Intent intent = new Intent(this, SettingsActivity.class);
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