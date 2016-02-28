package com.tengio.cpn;

import com.google.android.gms.gcm.GcmListenerService;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.io.Serializable;

public abstract class CpnNotificationService<T extends Serializable> extends GcmListenerService {

    public static final String NOTIFICATION_OBJECT = "NotificationObject";
    public static final String NOTIFICATION_RECEIVED = "com.cpn.action.NOTIFICATION_RECEIVED";
    public static final IntentFilter INTENT_FILTER = new IntentFilter(NOTIFICATION_RECEIVED);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Intent nri = new Intent(NOTIFICATION_RECEIVED);
        T t = getPushObject(data);
        nri.putExtra(NOTIFICATION_OBJECT, t);
        boolean received = LocalBroadcastManager.getInstance(this).sendBroadcast(nri);
        if (received) {
            return;
        }
        sendNotification(t);
    }

    protected abstract void sendNotification(T data);

    protected abstract T getPushObject(Bundle data);
}
