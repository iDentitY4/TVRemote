package de.sksystems.tvremote;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;

/**
 * Created by Manuel on 29.12.2017.
 */

public class App extends Application {

    public App() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new RemoteExceptionHandler(this));
    }
}
