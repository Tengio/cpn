package com.tengio.cpn;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.io.Serializable;

public abstract class CpnNotificationService<T extends Serializable> extends FirebaseMessagingService {

    public static final String NOTIFICATION_OBJECT = "NotificationObject";
    public static final String NOTIFICATION_RECEIVED = "com.cpn.action.NOTIFICATION_RECEIVED";
    public static final IntentFilter INTENT_FILTER = new IntentFilter(NOTIFICATION_RECEIVED);

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

    protected abstract void showNotification(T data);

    protected abstract T getPushObject(RemoteMessage remoteMessage);
}
