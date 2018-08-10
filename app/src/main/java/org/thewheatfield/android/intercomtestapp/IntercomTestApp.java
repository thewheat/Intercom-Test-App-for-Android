package org.thewheatfield.android.intercomtestapp;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import io.intercom.android.sdk.Intercom;

public class IntercomTestApp extends MultiDexApplication {
    public static final String TAG = "IntercomTestApp";
    private Settings settings;
    @Override public void onCreate() {
        super.onCreate();
        settings = new Settings(getApplicationContext());
        String app_id = settings.getValue(Settings.APP_ID);
        String sdk_api_key = settings.getValue(Settings.SDK_API_KEY);
        Log.i(TAG, "Initialise Intercom. App ID: " + app_id + " / SDK API Key: " + sdk_api_key);
        Intercom.initialize(this, sdk_api_key, app_id);
        Intercom.setLogLevel(Intercom.LogLevel.DEBUG);
    }
}
