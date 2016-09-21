package com.tengio.cpn;

public interface CpnInAppListener<T> {

    void onReceived(T serializableExtra);
}
