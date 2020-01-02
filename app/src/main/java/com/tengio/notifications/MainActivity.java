package com.tengio.notifications;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.tengio.cpn.CpnInAppListener;
import com.tengio.cpn.CpnInAppReceiver;

public class MainActivity extends AppCompatActivity {

    private CpnInAppReceiver<PushNotification> cpnActivityReceiver = new CpnInAppReceiver<>();
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = (TextView) findViewById(R.id.notification_message);
        /*FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("XXX",newToken);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        cpnActivityReceiver.register(this, new CpnInAppListener<PushNotification>() {
            @Override
            public void onReceived(PushNotification notification) {
                Log.e("XXX","Messgae is " + notification.getMessage());
                message.setText(notification.getMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        cpnActivityReceiver.unregister(this);
        super.onPause();
    }
}
