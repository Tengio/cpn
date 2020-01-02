package com.tengio.cpn;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;

public abstract class CpnNotificationService<T extends Serializable> extends FirebaseMessagingService {

    public static final String NOTIFICATION_OBJECT = "NotificationObject";
    public static final String NOTIFICATION_RECEIVED = "com.cpn.action.NOTIFICATION_RECEIVED";
    public static final IntentFilter INTENT_FILTER = new IntentFilter(NOTIFICATION_RECEIVED);

    private static final String TOKEN = "push_token";

    public CpnNotificationService() {
        initialise();
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    if (BuildConfig.DEBUG) {
                        Log.w("CPN", "getInstanceId failed", task.getException());
                    }
                    return;
                }
                // Get new Instance ID token
                InstanceIdResult token = task.getResult();
                // Log and toast
                if (token == null) {
                    if (BuildConfig.DEBUG) {
                        Log.d("CPN", "Token is null");
                    }
                    return;
                }
                onNewToken(token.getToken());
                if (BuildConfig.DEBUG) {
                    Log.d("CPN", token.getToken());
                }
            }
        });
    }

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        String token = getToken(preference);
        if (token == null || !token.equals(refreshedToken)) {
            saveToken(refreshedToken, preference);
            onTokenReady(refreshedToken);
        }
    }

    private String getToken(SharedPreferences preference) {
        return preference.getString(TOKEN, null);
    }

    private void saveToken(String refreshedToken, SharedPreferences preference) {
        final SharedPreferences.Editor editor = preference.edit();
        editor.putString(TOKEN, refreshedToken);
        editor.apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Intent nri = new Intent(NOTIFICATION_RECEIVED);
        T t = getPushObject(remoteMessage);
        nri.putExtra(NOTIFICATION_OBJECT, t);
        boolean received = LocalBroadcastManager.getInstance(this).sendBroadcast(nri);
        if (received) {
            return;
        }
        showNotification(t);
    }

    protected abstract void initialise();

    protected abstract void onTokenUpdate(String token);

    protected abstract void onTokenReady(String token);

    protected abstract void showNotification(T data);

    protected abstract T getPushObject(RemoteMessage remoteMessage);
}
