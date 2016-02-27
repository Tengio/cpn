package com.tengio.cpn;

public class CpnInstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        CpnRegistrationIntentService.start(this);
    }
}
