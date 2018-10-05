package org.thewheatfield.android.intercomdemoapp;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import io.intercom.android.sdk.Intercom;

public class IntercomDemoApp extends MultiDexApplication {
    public static final String TAG = "IntercomTestApp";
    private Settings settings;
    @Override public void onCreate() {
        super.onCreate();
        settings = new Settings(getApplicationContext());
        String app_id = settings.getValue(Settings.APP_ID);
        String sdk_api_key = settings.getValue(Settings.SDK_API_KEY);
        if(app_id.equals("") || sdk_api_key.equals("")){
            app_id = SettingsActivity.HC_app_id;
            sdk_api_key = SettingsActivity.HC_sdk_api_key;
            Log.i(TAG, "No data specified. Using default demp app. App ID: " + app_id);
        }
        else{
            Log.i(TAG, "Initialise Intercom with your custom values App ID: " + app_id);
        }
        Intercom.initialize(this, sdk_api_key, app_id);
        Intercom.setLogLevel(Intercom.LogLevel.DEBUG);
    }
}
