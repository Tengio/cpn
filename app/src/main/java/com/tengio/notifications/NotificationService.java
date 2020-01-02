package com.tengio.notifications;

import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.tengio.cpn.CpnNotificationService;

public class NotificationService extends CpnNotificationService<PushNotification> {

    public NotificationService(){
        super();
        Log.i("XXX", "Create notif service");
    }

    @Override
    protected void initialise() {

    }

    @Override
    protected void onTokenUpdate(String token) {

    }

    @Override
    protected void onTokenReady(String token) {

    }

    @Override
    protected void showNotification(PushNotification pushNotification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(pushNotification.getMessage())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    protected PushNotification getPushObject(RemoteMessage remoteMessage) {
        String message = remoteMessage.getNotification().getTitle();
        return new PushNotification(message);
    }
}
