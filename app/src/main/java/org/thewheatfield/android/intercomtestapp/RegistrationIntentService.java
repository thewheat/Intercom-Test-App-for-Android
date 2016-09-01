package org.thewheatfield.android.intercomtestapp.onex;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import io.intercom.android.sdk.Intercom;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Context ctx = getApplicationContext();
            Settings settings = new Settings(ctx);


            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(settings.getValue(Settings.GCM_SENDER_ID), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);
            settings.setValue(Settings.GCM_TOKEN, token);
            sendRegistrationToServer(token);
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
    }

    private void sendRegistrationToServer(String token) {
        Log.i(TAG, "GCM Registration Token sent to Intercom: " + token);
        Intercom.client().setupGCM(token, R.mipmap.ic_sdk);
        /*
        if icon doesn't exist
05-14 18:28:34.132 31711-32324/org.thewheatfield.android.intercomtestapp E/AndroidRuntime: FATAL EXCEPTION: IntentService[GcmIntentService]
                                                                                           Process: org.thewheatfield.android.intercomtestapp, PID: 31711
                                                                                           java.lang.IllegalArgumentException: Path must not be empty.
                                                                                               at io.intercom.com.squareup.picasso.Picasso.load(Picasso.java:297)
        *
        * */
    }
}
