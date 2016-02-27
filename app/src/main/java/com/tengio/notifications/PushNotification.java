package com.tengio.notifications;

import java.io.Serializable;

public class PushNotification implements Serializable {

    private final String message;

    public PushNotification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
