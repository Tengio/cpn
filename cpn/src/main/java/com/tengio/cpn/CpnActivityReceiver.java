package com.tengio.cpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.Serializable;

public class CpnActivityReceiver<T extends Serializable> {

    private BroadcastReceiver receiver;

    public void register(Context context, final CpnInAppListener<T> cpnInAppListener) {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Object obj = intent.getSerializableExtra(CpnNotificationListenerService.NOTIFICATION_OBJECT);
                if (obj == null) {
                    return;
                }
                try {
                    cpnInAppListener.onReceived((T) obj);
                } catch (Exception e) {
                    Log.w("cpn", "Problem reading notification object: " + e.getMessage());
                }
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, CpnNotificationListenerService.INTENT_FILTER);
    }

    public void unregister(Context context) {
        if (receiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        }
    }
}
