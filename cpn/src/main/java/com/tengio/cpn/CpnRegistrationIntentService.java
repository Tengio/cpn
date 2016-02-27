package com.tengio.cpn;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;

public abstract class CpnRegistrationIntentService extends IntentService {

    public static final String CPN_REGISTRATION_ACTION = "com.tengio.cpn.REGISTRATION_ACTION";
    private static final String TOKEN = "push_token";
    private static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    private static final String LOCK = "";

    public CpnRegistrationIntentService(Class<? extends  CpnRegistrationIntentService> clazz) {
        super(clazz.getSimpleName());
    }

    public static void start(Context context) {
        Intent intent = new Intent(CPN_REGISTRATION_ACTION);
        intent.setPackage(context.getPackageName());
        context.startService(intent);
    }

    public static String getGcmToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(CpnRegistrationIntentService.TOKEN, null);
    }

    public static boolean isGcmAlreadyRegistered(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            synchronized (LOCK) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getToken(this), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                boolean alreadyRegistered = isGcmAlreadyRegistered(this);
                final SharedPreferences.Editor editor = preference.edit();
                editor.putBoolean(SENT_TOKEN_TO_SERVER, true);
                editor.putString(TOKEN, token);
                editor.apply();
                onTokenReady(token, alreadyRegistered);
            }
        } catch (IOException e) {
            preference.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    protected abstract void onTokenReady(String token, boolean alreadyRegistered);

    protected abstract String getToken(CpnRegistrationIntentService cpnRegistrationIntentService);
}
