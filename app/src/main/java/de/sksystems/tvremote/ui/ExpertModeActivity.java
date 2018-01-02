package de.sksystems.tvremote.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import de.sksystems.tvremote.R;
import de.sksystems.tvremote.RemoteController;
import de.sksystems.tvremote.util.FragmentClickHandler;

public class ExpertModeActivity extends RemoteModeActivity {

    FragmentClickHandler mAdvancedControlBarClickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdvancedControlBarClickHandler = (AdvancedControlBarFragment) getFragmentManager().findFragmentById(R.id.control_bar_expert);
        ((ToggleButton) findViewById(R.id.control_pip)).setChecked(mRemoteControl.getTVState().isPip());
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
            case R.id.menuItemFavorites:
            {
                Intent intent = new Intent(this, EditFavouritesActivity.class);
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

    public void onAdvancedControlBarItemClick(View v) {
        mAdvancedControlBarClickHandler.onClick(v);
    }

    public static class AdvancedControlBarFragment extends Fragment implements FragmentClickHandler {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.controlbar_expert, container, false);
        }

        @Override
        public void onClick(View v) {

            RemoteController rc = RemoteController.getInstance(getContext());

            switch(v.getId()) {
                case R.id.control_pip: {
                    rc.showPip(((ToggleButton) v).isChecked());
                    break;
                }
                case R.id.control_aspect: {
                    rc.zoomMain();
                    break;
                }
            }
        }
    }
}
