package org.thewheatfield.android.intercomtestapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.intercom.android.sdk.Intercom;

import static android.Manifest.permission.READ_CONTACTS;


public class SettingsActivity extends AppCompatActivity {

    private Settings settings;
    public static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);
        settings = new Settings(getApplicationContext());
        loadDataFromSettings();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadDataFromSettings(){
        HashMap map = new HashMap();
        map.put(R.id.app_id, Settings.APP_ID);
        map.put(R.id.sdk_api_key, Settings.SDK_API_KEY);

        map.put(R.id.secret_key, Settings.SDK_SECURE_MODE_SECRET_KEY);

        map.put(R.id.gcm_api_key, Settings.GCM_API_KEY);
        map.put(R.id.gcm_sender_id, Settings.GCM_SENDER_ID);

        map.put(R.id.gcm_token, Settings.GCM_TOKEN);



        map.put(R.id.email_input, Settings.LAST_EMAIL);
        map.put(R.id.userid_input, Settings.LAST_USER_ID);

        map.put(R.id.custom_data_var, Settings.LAST_CUSTOM_ATTRIBUTE_NAME);
        map.put(R.id.custom_data_val, Settings.LAST_CUSTOM_ATTRIBUTE_VALUE);

        map.put(R.id.event_name, Settings.LAST_EVENT_NAME);
        map.put(R.id.event_metadata_name, Settings.LAST_EVENT_METADATA_NAME);
        map.put(R.id.event_metadata_value, Settings.LAST_EVENT_METADATA_VALUE);


        Set set = map.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry item = (Map.Entry)i.next();
            setData((int) item.getKey(), settings.getValue((String) item.getValue()));
        }
    }


    private String getData(int id){
        EditText tmp = (EditText) findViewById(id);
        return tmp.getText().toString();
    }
    private void setData(int id, String data){
        EditText tmp = (EditText) findViewById(id);
        if(tmp != null) tmp.setText(data);
    }

    public void onClickSaveAll(View v){
        onClickSaveSecretKey(v);
        onClickSavePushSettings(v);
        onClickSaveAppSettings(v);
    }
    public void onClickSaveAppSettings(View v){

        String app_id = getData(R.id.app_id);
        String sdk_api_key = getData(R.id.sdk_api_key);
        Log.i(TAG, "Update App Settings. App ID: " + app_id + " / SDK API Key: " + sdk_api_key);

        String oldData = settings.getValue(Settings.APP_ID) + "_" + settings.getValue(Settings.SDK_API_KEY);
        settings.setValue(Settings.APP_ID, app_id);
        settings.setValue(Settings.SDK_API_KEY, sdk_api_key);
        String newData = settings.getValue(Settings.APP_ID) + "_" + settings.getValue(Settings.SDK_API_KEY);
        if(!oldData.equals(newData)){ // something has changed. Need to restart

            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setMessage(R.string.confirm_close);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    // Ideally this should close the app and restart it and reinitialise the SDK but I can't seem to get this to work. Not enough Android-fu to get it working yet. So just prompt user to close it themselves
                    // TODO: allow this to completely close the app and reopen it with new SDK setting enabled
                    // Logcat info that shows on proper reinitialising of app
                    // 05-15 15:20:15.774 7889-7889/org.thewheatfield.android.intercomtestapp E/GMPM: GoogleService failed to initialize, status: 10, Missing an expected resource: 'R.string.google_app_id' for initializing Google services.  Possible causes are missing google-services.json or com.google.gms.google-services gradle plugin.
                    // 05-15 15:20:15.774 7889-7889/org.thewheatfield.android.intercomtestapp E/GMPM: Scheduler not set. Not logging error/warn.
                    // 05-15 15:20:15.870 7889-7941/org.thewheatfield.android.intercomtestapp E/GMPM: Uploading is not possible. App measurement disabled

//                    // close this and parent activities
//                    Log.i(TAG, "Closing app and restarting to initialise SDK");
//                    setResult(RESULT_OK, null);
//                    finish();
//
//                    // restart app
//                    final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                            startActivity(intent);
//                        }
//                    }, 2000);

                }
            });
//            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int id) {
//                    Log.i(TAG, "Dismissed. Not closing");
//                }
//            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }



    public void onClickSaveSecretKey(View v){
        String secret = getData(R.id.secret_key);
        Log.i(TAG, "Save Secure Mode. Secret Key: " + secret);
        settings.setValue(Settings.SDK_SECURE_MODE_SECRET_KEY, secret);
    }

    public void onClickSavePushSettings(View v){
        String gcm_sender_id = getData(R.id.gcm_sender_id);
        String gcm_api_key = getData(R.id.gcm_api_key);
        Log.i(TAG, "Save GCM Settings. GCM API Key: " + gcm_api_key+ " / GCM Sender ID: " + gcm_sender_id);
        settings.setValue(Settings.GCM_API_KEY, gcm_api_key);
        settings.setValue(Settings.GCM_SENDER_ID, gcm_sender_id);
    }



}

