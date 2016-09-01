package org.thewheatfield.android.intercomtestapp.onex;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class SettingsActivity extends AppCompatActivity {

    private Settings settings;
    public static final String TAG = "SettingsActivity";

    // hard code values into your app so you don't need to manually copy and paste them
    private String HC_app_id = "";
    private String HC_sdk_api_key = "";
    private String HC_secret_key = "";
    private String HC_gcm_api_key = "";
    private String HC_gcm_sender_id = "";



    CheckBox ignoreHardcodedValuesCheckbox = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        setContentView(R.layout.activity_settings);
        ignoreHardcodedValuesCheckbox = (CheckBox) findViewById(R.id.checkbox_ignore_hardcoded_values);
        LinearLayout panelHardcoded = (LinearLayout) findViewById(R.id.panel_hardcoded_values);


        settings = new Settings(getApplicationContext());
        loadDataFromSettings();
        if(getIgnoreHardcodedValuesSettings()){
            if(hasHardcodedValue()) {
                panelHardcoded.setVisibility(View.VISIBLE);
            }
            else{
                panelHardcoded.setVisibility(View.INVISIBLE);
            }
        }
        else{
            if(hasHardcodedValue()){
                saveDataFromHarcodedValues();
                loadDataFromSettings(); // reload with new data
                panelHardcoded.setVisibility(View.VISIBLE);
            }
            else{
                panelHardcoded.setVisibility(View.GONE);
            }
        }
        onNewIntent(getIntent());

    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            Log.d(TAG, "Intent received, with data: " + data);
            loadDataFromDeepLink(data);
        }
    }

    private void loadDataFromDeepLink(String data){
        Uri uri = Uri.parse(data);
        HashMap map = new HashMap();

        map.put("app_id", R.id.app_id);
        map.put("sdk_api_key", R.id.sdk_api_key);
        map.put("secret_key", R.id.secret_key);
        map.put("gcm_api_key", R.id.gcm_api_key);
        map.put("gcm_sender_id", R.id.gcm_sender_id);

        Set set = map.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry item = (Map.Entry)i.next();
            String key = (String) item.getKey();
            int ui_id = (int) item.getValue();
            String value = uri.getQueryParameter(key);
            if(value != null && !value.equals("")){
                setData(ui_id, value);
            }
        }
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

    private boolean getIgnoreHardcodedValuesSettings(){
        return settings.getBoolean(Settings.IGNORE_HARDCODED_VALUES);
    }
    private boolean hasHardcodedValue(){
        return (!HC_app_id.equals("")
        || !HC_sdk_api_key.equals("")
        || !HC_secret_key.equals("")
        || !HC_gcm_api_key.equals("")
        || !HC_gcm_sender_id.equals("")
        );
    }
    private void saveDataFromHarcodedValues(){
        if(!HC_app_id.equals(""))         settings.setValue(Settings.APP_ID                     , HC_app_id);
        if(!HC_sdk_api_key.equals(""))    settings.setValue(Settings.SDK_API_KEY                , HC_sdk_api_key);
        if(!HC_secret_key.equals(""))     settings.setValue(Settings.SDK_SECURE_MODE_SECRET_KEY , HC_secret_key);
        if(!HC_gcm_api_key.equals(""))    settings.setValue(Settings.GCM_API_KEY                , HC_gcm_api_key);
        if(!HC_gcm_sender_id.equals(""))  settings.setValue(Settings.GCM_SENDER_ID              , HC_gcm_sender_id);
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

        ignoreHardcodedValuesCheckbox.setChecked(getIgnoreHardcodedValuesSettings());
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


        if(hasHardcodedValue()){ // only check hardcoded values if we have any
            settings.setValue(Settings.IGNORE_HARDCODED_VALUES, ignoreHardcodedValuesCheckbox.isChecked());
            if(!ignoreHardcodedValuesCheckbox.isChecked()){
                saveDataFromHarcodedValues();
                loadDataFromSettings();
            }
        }


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

