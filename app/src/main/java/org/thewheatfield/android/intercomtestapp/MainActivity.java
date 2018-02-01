package org.thewheatfield.android.intercomtestapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.UnreadConversationCountListener;
import io.intercom.android.sdk.UserAttributes;
import io.intercom.android.sdk.identity.Registration;

public class MainActivity extends AppCompatActivity implements UnreadConversationCountListener {


    public static final String TAG = "MainActivity";
    private Settings settings;
    private TextView txtPushLog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        settings = new Settings(getApplicationContext());
        setContentView(R.layout.activity_main);
        txtPushLog = (TextView) findViewById(R.id.txtPushLog);
        onCreateInitialiseIntercom();
        loadDataFromSettings();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        onNewIntent(getIntent());

        checkRedirectToSettings();

    }
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            Uri uri = Uri.parse(data);
            if(uri.getHost().equalsIgnoreCase("settings")) {
                startSettingsActivity(intent.getData());
            }
            else if(uri.getHost().equalsIgnoreCase("test_gcm_token")) {
                String token = uri.getLastPathSegment();
                if(token != null && !token.equals(""))
                    setData(R.id.gcm_token, token);
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            startSettingsActivity();
            return true;
        }
        else if (id == R.id.action_show_conversations) {
            Intercom.client().displayConversationsList();
            return true;
        }
        else if (id == R.id.action_compose_message) {
            Intercom.client().displayMessageComposer();
            return true;
        }
        else if (id == R.id.action_display_help_center){
            Intercom.client().displayHelpCenter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void checkRedirectToSettings(){
        if(settings.getValue(Settings.APP_ID).trim().equals("") || settings.getValue(Settings.SDK_API_KEY).trim().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(R.string.confirm_redirect_to_settings);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.i(TAG, "Redirecting to settings to specify SDK config");
                    startSettingsActivity();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Log.i(TAG, "Dismissed. Not redirecting");
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    private void startSettingsActivity(){
        startSettingsActivity(null);
    }

    private void startSettingsActivity(Uri uri){
        Intent intent = new Intent(this, SettingsActivity.class);
        if(uri != null) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
        }
        startActivity(intent);
        // startActivityForResult(intent, REQUEST_EXIT);
    }

    private void loadDataFromDeepLink(String data){
        Uri uri = Uri.parse(data);
        HashMap map = new HashMap();

        map.put("app_id", R.id.app_id);
        map.put("sdk_api_key", R.id.sdk_api_key);
        map.put("secret_key", R.id.secret_key);
        map.put("gcm_api_key", R.id.gcm_api_key);

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

//    // part of trying to close app to reinitialise SDK on update of value. Not working - 1
//    private int REQUEST_EXIT = 1;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == REQUEST_EXIT) {
//            if (resultCode == RESULT_OK) {
//                this.finish();
//
//            }
//        }
//    }
//    // part of trying to close app to reinitialise SDK on update of value. Not working - 0

    private String getData(int id){
        EditText tmp = (EditText) findViewById(id);
        return tmp.getText().toString();
    }
    private void setData(int id, String data){
        EditText tmp = (EditText) findViewById(id);
        if(tmp != null) tmp.setText(data);
    }
    private void loadDataFromSettings(){
        HashMap map = new HashMap();
        map.put(R.id.app_id, Settings.APP_ID);
        map.put(R.id.sdk_api_key, Settings.SDK_API_KEY);

        map.put(R.id.secret_key, Settings.SDK_SECURE_MODE_SECRET_KEY);

        map.put(R.id.gcm_api_key, Settings.GCM_API_KEY);

        map.put(R.id.gcm_token, Settings.GCM_TOKEN);

        map.put(R.id.name_input, Settings.LAST_NAME);
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


    public void intercomCheckSecureMode(String data){
        String secret = settings.getValue(Settings.SDK_SECURE_MODE_SECRET_KEY);
        Log.i(TAG, "Check secure mode. Data: " + data + " / Set Secure mode?: " + (!secret.equals("")));
        if(!secret.equals("")) {
            Intercom.client().setUserHash(generateHash(data, secret));
        }
    }
    public void onCreateInitialiseIntercom(){
        // main Intercom library initalisation done in the Custom Application file as per docs
        // https://docs.intercom.io/install-on-your-product-or-site/quick-install/install-intercom-on-your-android-app

        Intent intent = getIntent();
        if(intent != null){
            String action = intent.getAction();
            String data = intent.getDataString();
            Bundle extras = intent.getExtras();
            if(extras == null) extras = new Bundle();
            Log.i(TAG, "Intent. action: " + action + " |data: " + data + "|extras:" + extras.toString());
        }

        Intercom.client().handlePushMessage();
    }
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // GENERATING SECRET HASH SHOULD NEVER BE DONE CLIENT SIDE (ON ANY PLATFORM) IN A PRODUCTION APP
    // SECRET KEY IS MEANT TO BE ON YOUR SERVER END AND NEEDS TO BE KEPT SECRET
    // ONLY INCLUDED HERE TO ENABLE SELF ENCLOSED APP FOR TESTING WITHOUT NEEDED SERVER CHANGES
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public String generateHash(String message, String secret) {
        try {

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = (sha256_HMAC.doFinal(message.getBytes()));
            StringBuffer result = new StringBuffer();
            for (byte b : hash) {
                result.append(String.format("%02X", b));
            }
            return result.toString().toLowerCase(); // lower case needed for mobile
        }
        catch (Exception e){
            System.out.println("Error");
        }
        return "";
    }


    public void onClickSignIn(View v){
        String email = getData(R.id.email_input);
        String user_id = getData(R.id.userid_input);
        String name = getData(R.id.name_input);
        settings.setValue(Settings.LAST_NAME, name);
        settings.setValue(Settings.LAST_EMAIL, email);
        settings.setValue(Settings.LAST_USER_ID, user_id);

        Log.i(TAG, "Sign in as registered user. email: " + email + " / user_id: " + user_id);
        Registration registration = new Registration();
        String data = email;
        UserAttributes userAttributes = null;
        if(!name.equals("")) {
            userAttributes = new UserAttributes.Builder().withName(name).build();
            registration = registration.withUserAttributes(userAttributes);
        }
        if(!email.equals(""))  registration = registration.withEmail(email);
        if(!user_id.equals(""))  {
            registration = registration.withUserId(user_id);
            data = user_id;
        }
        intercomCheckSecureMode(data);
        Intercom.client().registerIdentifiedUser(registration);
    }

    public void onClickSignInUnidentified(View v){
        Log.i(TAG, "Sign in as unregistered user");
        Intercom.client().registerUnidentifiedUser();
    }
    public void onClickLogout(View v){
        Log.i(TAG, "Logout");
        Intercom.client().reset();
    }
    public void onClickUpdateAttribute(View v){
        // TODO: Enable adding non string values of events (e.g. numbers, dates)
        String value = getData(R.id.custom_data_val);
        String name = getData(R.id.custom_data_var);
        settings.setValue(Settings.LAST_CUSTOM_ATTRIBUTE_NAME, name);
        settings.setValue(Settings.LAST_CUSTOM_ATTRIBUTE_VALUE, value);
        RadioButton typeStandard = (RadioButton) findViewById(R.id.radioButtonStandard);


        Log.i(TAG, "Update Attribute. Name: " + name + " / Value: " + value + " / Type: " + (typeStandard.isChecked() ? "Standard" : "Custom"));
        UserAttributes userAttributes = null;
        boolean found = false;
        if(typeStandard.isChecked()){
            switch(name.toLowerCase()){
                case "name":
                    found = true;
                    userAttributes = new UserAttributes.Builder().withName(value).build();
                    break;
                case "email":
                    found = true;
                    userAttributes = new UserAttributes.Builder().withEmail(value).build();
                    break;
                case "phone":
                    found = true;
                    userAttributes = new UserAttributes.Builder().withPhone(value).build();
                    break;
                case "languageoverride":
                case "language_override":
                    found = true;
                    userAttributes = new UserAttributes.Builder().withLanguageOverride(value).build();
                    break;
                case "signed_up_at":
                case "created_at":
                case "remote_created_at":
                    long date = 0;
                    try{
                        date= Long.parseLong(value);
                        userAttributes = new UserAttributes.Builder().withSignedUpAt(date).build();
                        found = true;
                    }
                    catch(NumberFormatException ex){
                        Toast.makeText(getApplicationContext(), "Invalid date value use a Unix timestamp", Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case "unsubscribed_from_emails":
                    found = true;
                    userAttributes = new UserAttributes.Builder().withUnsubscribedFromEmails(value.toLowerCase().equals("true")).build();
                    break;
            }
        }
        else{
            userAttributes = new UserAttributes.Builder().withCustomAttribute(name, value).build();
        }
        if(found && userAttributes != null){
            Intercom.client().updateUser(userAttributes);
        }else{
            Toast.makeText(getApplicationContext(), "Invalid standard attribute: " + Intercom.client().getUnreadConversationCount(), Toast.LENGTH_LONG).show();
        }
    }

    public void onClickSubmitEvent(View v){
        // TODO: Enable adding non string values of metadata (e.g. numbers, dates)
        String name = getData(R.id.event_name);
        String metadata_name = getData(R.id.event_metadata_name);
        String metadata_value = getData(R.id.event_metadata_value);
        settings.setValue(Settings.LAST_EVENT_NAME, name);
        settings.setValue(Settings.LAST_EVENT_METADATA_NAME, metadata_name);
        settings.setValue(Settings.LAST_EVENT_METADATA_VALUE, metadata_value);
        Log.i(TAG, "Submit Event. Name: " + name + " / Meta Data Name: " + metadata_name + " / Meta Data Value: " + metadata_value);
        if(!metadata_name.equals("") && !metadata_value.equals("")){
            Map eventData = new HashMap();
            eventData.put(metadata_name, metadata_value);
            Intercom.client().logEvent(name, eventData);
        }
        else {
            Intercom.client().logEvent(name);
        }
    }
    public void onClickShowConversations(View v) {
        Intercom.client().displayConversationsList();
    }
    public void onClickShowComposer(View v) { Intercom.client().displayMessageComposer(getData(R.id.message_prefill_value)); }
    public void onClickHide(View v) {
        Intercom.client().setInAppMessageVisibility(Intercom.GONE);
    }
    public void onClickSetPadding(View v) {
        Intercom.client().setBottomPadding(Integer.parseInt(getData(R.id.bottom_padding)));
    }
    public void onClickShow(View v) {
        Intercom.client().setInAppMessageVisibility(Intercom.VISIBLE);
    }
    public void onClickMessengerHide(View v) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intercom.client().hideMessenger();
            }
        }, 5000);
    }
    public void onClickMessengerShow(View v) {
        Intercom.client().displayMessenger();
    }
    public void onClickLauncherShow(View v) {
        Intercom.client().setLauncherVisibility(Intercom.Visibility.VISIBLE);
    }
    public void onClickLauncherHide(View v) {
        Intercom.client().setLauncherVisibility(Intercom.Visibility.GONE);
    }
    public void onClickUnreadReadListenerStart(View v) {
        Intercom.client().addUnreadConversationCountListener(this);
    }
    public void onClickUnreadReadListenerStop(View v) {
        Intercom.client().removeUnreadConversationCountListener(this);
    }
    public void onClickUnreadReadCount(View v) {
        Toast.makeText(getApplicationContext(), "Unread count: " + Intercom.client().getUnreadConversationCount(), Toast.LENGTH_LONG).show();
    }

    public void onClickDisplayHelpCenter(View v) {
        Intercom.client().displayHelpCenter();
    }

    public void pushLog(String msg){
        txtPushLog.setText(msg);
    }
    public void onClickTestPushNotification(View v){
        Log.i(TAG, "Prompt to send test push notification");
        pushLog("Sending push notification...");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.confirm_push_notification);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String token = getAndSaveToken();
                final String output = "{\"registration_ids\": [\""+ token + "\"], "+
                        "\"data\": {"+
                        "\"image_url\": \"https://intercom.io/test.jpg\","+
                        "\"intercom_push_type\": \"intercom_push\"," +
                        "\"receiver\": \"intercom_sdk\"," +
                        "\"title\": \"Push Notification for in-app\", " +
                        "\"author_name\": \"in-app message\", " +
                        "\"body\":\"Push notification!\"" +
                        "}}";
                Log.i(TAG, "Minimizing app and sending push notification...: " + output);
                minimizeApp();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new SendPushMessage().execute(settings.getValue(Settings.GCM_API_KEY), output);
                    }
                }, 500);


            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i(TAG, "Dismissed. Push notification not sent");
                pushLog("Not putting app in background. Push notification will not be received");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void onClickTestPushMessage(View v){
        String token = getAndSaveToken();
        String output = "{\"registration_ids\": [\""+ token + "\"], " +
                "\"data\": {" +
                "\"image_url\": \"https://interocm.io/test.jpg\"," +
                "\"intercom_push_type\": \"push_only\", " +
                "\"receiver\": \"intercom_sdk\", " +
                "\"title\": \"Push Message\", " +
                "\"body\":\"Push message to sms:123\"," +
                " \"uri\": \"sms:123\"" +
                "}}";
        Log.i(TAG, "Send test push message: " + output);
        pushLog("Sending push message...");
        new SendPushMessage().execute(settings.getValue(Settings.GCM_API_KEY), output);
    }

    public String getAndSaveToken(){
        String token = getData(R.id.gcm_token);
        settings.setValue(settings.GCM_TOKEN, token);
        return token;
    }
    public void onClickGCMUseMyToken(View v){
        setData(R.id.gcm_token, FirebaseInstanceId.getInstance().getToken());
    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void onCountUpdate(int i) {
        Toast.makeText(getApplicationContext(), "Unread count listener:: " + i, Toast.LENGTH_LONG).show();
    }


    private class SendPushMessage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            try {
                String API_KEY = urls[0];
                String output = urls[1];
                URL url = new URL("https://android.googleapis.com/gcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "key=" + API_KEY);
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                outputStream.writeBytes(output);
                outputStream.flush();
                outputStream.close();
                BufferedReader responseStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = responseStream.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                responseStream.close();
                return response.toString();

            } catch (IOException e) {
                e.printStackTrace();
                return "ERROR: " + e.getMessage();
            }
            finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Push output: " + result);
            pushLog("Push output: " + result);
        }
    }
}
