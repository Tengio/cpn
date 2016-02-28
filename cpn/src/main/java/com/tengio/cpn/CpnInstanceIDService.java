package com.tengio.cpn;

public class CpnInstanceIDService extends com.google.android.gms.iid.InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        CpnRegistrationService.start(this);
    }
}
