package com.tengio.cpn;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class CpnRegistrationService extends FirebaseInstanceIdService {

    private static final String TOKEN = "push_token";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preference.edit();
        editor.putString(TOKEN, refreshedToken);
        editor.apply();
        onTokenReady(refreshedToken);
    }

    protected abstract void onTokenReady(String token);
}
