package com.tengio.notifications;

import com.tengio.cpn.CpnRegistrationService;

public class RegistrationService extends CpnRegistrationService {

    @Override
    protected void onTokenReady(String token) {
        //TODO send the token to your server base your logic on alreadyRegistered flag
    }
}
