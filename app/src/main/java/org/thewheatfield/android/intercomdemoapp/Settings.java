package org.thewheatfield.android.intercomdemoapp;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

    public static final String STORE_KEY = "my_gcm_details";
    public static final String SDK_API_KEY = "SDK_API_KEY";
    public static final String APP_ID= "APP_ID";
    public static final String SDK_SECURE_MODE_SECRET_KEY = "SDK_SECURE_MODE_SECRET_KEY";
    public static final String GCM_API_KEY = "GCM_API_KEY";
    public static final String GCM_SENDER_ID = "GCM_SENDER_ID";
    public static final String GCM_TOKEN = "GCM_TOKEN";


    public static final String LAST_EMAIL= "LAST_EMAIL";
    public static final String LAST_NAME = "LAST_NAME";
    public static final String LAST_USER_ID = "LAST_USER_ID";
    public static final String LAST_CUSTOM_ATTRIBUTE_NAME = "LAST_CUSTOM_ATTRIBUTE_NAME";
    public static final String LAST_CUSTOM_ATTRIBUTE_VALUE= "LAST_CUSTOM_ATTRIBUTE_VALUE";
    public static final String LAST_EVENT_NAME = "LAST_EVENT_NAME";
    public static final String LAST_EVENT_METADATA_NAME = "LAST_EVENT_METADATA_NAME";
    public static final String LAST_EVENT_METADATA_VALUE = "LAST_EVENT_METADATA_VALUE";


    public static final String IGNORE_HARDCODED_VALUES = "IGNORE_HARDCODED_VALUES";


    private Context context;

    public Settings(Context context){
        this.context = context;
    }
    public String getValue(String key){
        SharedPreferences prefs = context.getSharedPreferences(Settings.STORE_KEY, context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }
    public boolean getBoolean(String key){
        SharedPreferences prefs = context.getSharedPreferences(Settings.STORE_KEY, context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }
    public boolean setValue(String key, String value){
        SharedPreferences prefs = context.getSharedPreferences(Settings.STORE_KEY, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        return editor.commit();
    }
    public boolean setValue(String key, boolean value){
        SharedPreferences prefs = context.getSharedPreferences(Settings.STORE_KEY, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }
}
