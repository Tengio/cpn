CPN
===

This is a small library that wraps Google Cloud Messaging solution to make it easier to implement it.
It also add the ability to consume notification if the app is running at the moment of receiving the notification 
without additional work.

Version
-------

[ ![Download](https://api.bintray.com/packages/tengioltd/maven/cpn/images/download.svg) ](https://bintray.com/tengioltd/maven/cpn/_latestVersion)

Current version uses Google Play Services 8.4.0.

Version will follows google play services version so that it is going to ve even easier to implement.


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
        classpath 'com.google.gms:google-services:2.0.0-beta5'
    }
}
```
In the app build.gradle at the very bottom of the file (this it is probably just a temporary bug in google play 
services plugin) place:
```
apply plugin: 'com.google.gms.google-services'
```

Android manifest
----------------

First you need to add permissions :
```
<permission android:name="com.bpl.lpq.permission.C2D_MESSAGE"
            android:protectionLevel="signature"/>
<uses-permission android:name="com.bpl.lpq.permission.C2D_MESSAGE"/>
```

Add Services and receiver in the AndroidManifest.xml :
```
<meta-data android:name="com.google.android.gms.version"
           android:value="@integer/google_play_services_version"/>
<receiver android:name="com.tengio.cpn.CpnNotificationReceiver"
          android:exported="true"
          android:permission="com.google.android.c2dm.permission.SEND">
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE"/>
        <category android:name="${applicationId}"/>
    </intent-filter>
</receiver>
<service android:name="com.tengio.cpn.CpnInstanceIDService"
         android:exported="false">
    <intent-filter>
        <action android:name="com.google.android.gms.iid.InstanceID"/>
        <category android:name="${applicationId}"/>
    </intent-filter>
</service>
```

Google configuration file
-------------------------

Go to https://developers.google.com/mobile/add get the json for you add with google messaging service enabled.
Add Json to the app root folder.


RegistrationService
-------------------

Your app need to expose a registration intent service where the we can get the gcm sender id and you can send the 
token to the server when ready.
```java
public class RegistrationService extends CpnRegistrationService {

    public RegistrationService() {
        super(RegistrationService.class);
    }

    @Override
    protected void onTokenReady(String token, boolean alreadyRegistered) {
        Log.v("cpn", "onTokenReady: " + token);
        //TODO send the token to your server base your logic on alreadyRegistered flag
    }

    @Override
    protected String getToken() {
        return getString(R.string.gcm_defaultSenderId);
    }
}
```

Don't forget to add the registration service to the manifest
```
<service android:name=".RegistrationService"
                 android:exported="false">
    <intent-filter>
        <action android:name="com.tengio.cpn.REGISTRATION_ACTION"/>
        <category android:name="${applicationId}"/>
    </intent-filter>
</service>
```

Registration service is important as it lets you register the token and send it to your server.
But to do that you should make sure it is started every time to make sure your app is registered.

```
RegistrationIntentService.start(context);
```

 
NotificationService
-------------------

If you want to show push notification while the app is not active :
```java
public class NotificationService extends CpnNotificationService<PushNotification> {

    @Override
    protected void sendNotification(PushNotification pushNotification) {
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
    protected PushNotification getPushObject(Bundle data) {
        String message = data.getString("message");
        return new PushNotification(message);
    }
}
```
 
Also you need to add it to the manifest:
```
<service android:name=".NotificationService"
         android:exported="false">
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE"/>
        <category android:name="${applicationId}"/>
    </intent-filter>
</service>
```

Consume Notification in while app running
-----------------------------------------

If you want to consume notifications while app is running in an activity you can easily do that:

```
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

 
How to test local implementation
--------------------------------

It is possible to test the local implementation of the listeners easily with adb am tool.
The only issue you need to is to remove permission android:permission="com.google.android.c2dm.permission.SEND".

```
adb shell am broadcast -c com.tengio.notifications -a com.google.android.c2dm.intent.RECEIVE
```

if you need to pass key/value pair to the bundle you can use things like this:
```
-e collapse_key "ACTION_BOUND_TO_TABLE"
```
example:

```
adb shell am broadcast -c com.adzuna.debug -a com.google.android.c2dm.intent.RECEIVE -e context "jobs" -e id "6439443" -e message "hello" --ei new_count 324
```

How to test Google Cloud Messaging
----------------------------------

if you want to test the implementation with by receiving real push notification try this curl command:

```
curl --header "Authorization: key=AIzaSyAXdTTj4DSGfvm94CC66tXSH1OSRjL3UyQ" --header "Content-Type: application/json" 
    https://android.googleapis.com/gcm/send -d 
    "{\"registration_ids\":[\"fyfUV30o3CU:APA91bFZMO-HJxoX-y-VbKnesMrEkd02Hk2cIkuIMR45QtJoy5jiD1mEaJgBkqWhP5Scq3pkbo2jGZRlpiYIAO6RXS2XNxmGKs1aAnj6hnCBwyXzLfLjozbXlvYBvJGtsF229pVyR1OY\"]}"
```

real push with parameters:

```
curl --header "Authorization: key=AIzaSyDCCPu_DhK8QkgZ6kCU0xvQK6-YECC0Yzg" --header "Content-Type: application/json"
https://android.googleapis.com/gcm/send -d  
"{\"registration_ids\":[\"dgapK87n1oc:APA91bERPFkHTY0sYDpsoncFUFBwDqnFSFapXaSch-3prWOUy3IPcWAdP0YadzurJ8R0sSLObH_U4tsnToFVc6FDjjymjgsF9Xbts6ML8PFzuI0W2dLRgSWVM9fy5Z7DAA5TR_iUuZ9F\"], \"data\":{\"message\":\"You have 10 new jobs…..\", \"new_count\":3, \"context\":\"jobs\", \"id\": 6439443}}"
```

Library updates
---------------

We use bintray to deploy changes to jcenter. To deploy a new version make sure to define BINTRAY_USER and BINTRAY_KEY
 variables. Then run:
 
```
gradle bintrayUpload
```




