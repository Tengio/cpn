CPN
===

This is a small library that wraps Cloud Messaging solution to make it easier to implement it or just as an example.
It also add the ability to consume notification if the app is running at the moment of receiving the notification 
without additional work.

Version
-------

[ ![Download](https://api.bintray.com/packages/tengioltd/maven/cpn/images/download.svg) ](https://bintray.com/tengioltd/maven/cpn/_latestVersion)

Current version uses Firebase Cloud Messaging 9.6.0.

Old GCM version can be see at [gcm tag](https://github.com/Tengio/cpn/tree/gcm).

Version will follows google play services version so that it is going to ve even easier to implement.


Change Log:
-----------

9.6.0 :

- Firebase dependency updated to 9.6.0

9.4.0 :

- Firebase cloud messaging


9.2.1 is last version using GCM.

9.2.1 :

- Look like there is there is a new field in the google services json file :
```
"api_key": [ { "current_key": "" } ]
```
without you get "Missing api_key/current key with Google Services 3.0.0"
- firebase is added by google play services, this add more dependencies which may require you to add multiDex support
```
defaultConfig {
    multiDexEnabled true
}
```
- note play service 3.0.0 force you to use firebase 9.0.0. To avoid duplication with 9.2.1, cpn library manually add firebase 9.2.1.

8.4.0 :

- 8.4.0 Google Play Services 8.4.0.


HOW TO
======

Dependencies
------------

```
dependencies {
    ...
    compile 'com.tengio:cpn:latest_version'
}
```

You by adding cpn library dependency you will automatically get the following dependencies:

Gradle Plugins
--------------

In the root build.gradle file add: 
```
buildscript {
...
    dependencies {
        ...
        classpath 'com.google.gms:google-services:3.0.0'
    }
}
```
In the app build.gradle at the very bottom of the file (this it is probably just a temporary bug in google play 
services plugin) place:
```
apply plugin: 'com.google.gms.google-services'
```

Google configuration file
-------------------------

Go to https://developers.google.com/mobile/add get the json for you add with google messaging service enabled.
Add Json to the app root folder.


RegistrationService
-------------------

Your app need to expose a registration service where it is possible to send the token to your server implementation.
```java
public class RegistrationService extends CpnRegistrationService {

    @Override
    protected void onTokenReady(String token) {
        //TODO send the token to your server base your logic on alreadyRegistered flag
    }
}
```

Don't forget to add the registration service to the manifest

```xml
<service android:name="com.tengio.notifications.RegistrationService">
    <intent-filter>
        <action android:name="com.google.firebase.INSTANCE_ID_EVENT"/>
    </intent-filter>
</service>
```

 
NotificationService
-------------------

If you want to show push notification while the app is not active :
```java
public class NotificationService extends CpnNotificationService<PushNotification> {

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
        String message = remoteMessage.getData().get("message");
        return new PushNotification(message);
    }
}
```
 
Also you need to add it to the manifest:
```xml
<service android:name="com.tengio.notifications.NotificationService">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT"/>
    </intent-filter>
</service>
```

Consume Notification in while app running
-----------------------------------------

If you want to consume notifications while app is running in an activity you can easily do that:

```java
private CpnInAppReceiver<PushNotification> cpnReceiver = new CpnInAppReceiver<>();

@Override
protected void onResume() {
    super.onResume();
    cpnReceiver.register(this, new CpnInAppListener<PushNotification>() {
        @Override
        public void onReceived(PushNotification notification) {
            message.setText(notification.getMessage());
        }
    });
}

@Override
protected void onPause() {
    cpnReceiver.unregister(this);
    super.onPause();
}
```

How to test Firebase Cloud Messaging
------------------------------------

From the firebase console you can easily send notification to a specific application.


Library updates
---------------

We use bintray to deploy changes to jcenter. To deploy a new version make sure to define BINTRAY_USER and BINTRAY_KEY
 variables. Then run:
 
```
gradle bintrayUpload
```




