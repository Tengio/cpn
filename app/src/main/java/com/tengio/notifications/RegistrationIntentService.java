package com.tengio.notifications;

import android.util.Log;

import com.tengio.cpn.CpnRegistrationIntentService;

public class RegistrationIntentService extends CpnRegistrationIntentService {

    public RegistrationIntentService() {
        super(RegistrationIntentService.class);
    }

    @Override
    protected void onTokenReady(String token, boolean alreadyRegistered) {
        Log.v("cpn", "onTokenReady: " + token);
        //TODO send the token to your server base your logic on alreadyRegistered flag
    }

    @Override
    protected String getToken(CpnRegistrationIntentService cpnRegistrationIntentService) {
        return getString(R.string.gcm_defaultSenderId);
    }
}
