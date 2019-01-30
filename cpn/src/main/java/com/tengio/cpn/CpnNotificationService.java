package com.tengio.cpn;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import java.io.Serializable;

public abstract class CpnNotificationService<T extends Serializable> extends FirebaseMessagingService {

    public static final String NOTIFICATION_OBJECT = "NotificationObject";
    public static final String NOTIFICATION_RECEIVED = "com.cpn.action.NOTIFICATION_RECEIVED";
    public static final IntentFilter INTENT_FILTER = new IntentFilter(NOTIFICATION_RECEIVED);

    private static final String TOKEN = "push_token";

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preference.edit();
        editor.putString(TOKEN, refreshedToken);
        editor.apply();
        onTokenReady(refreshedToken);
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

    protected abstract void onTokenReady(String token);

    protected abstract void showNotification(T data);

    protected abstract T getPushObject(RemoteMessage remoteMessage);
}
