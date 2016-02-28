package com.tengio.notifications;

import android.util.Log;

import com.tengio.cpn.CpnRegistrationService;

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
