package com.bp.app.event.consumer.notification;

import java.util.List;
import java.util.Map;

public class SmsNotification implements Notification{

    @Override
    public boolean send(final Map<String, ?> dataToSend) {
        return true;
    }

    @Override
    public boolean sendAll(final List<Map<String, ?>> dataListToSend) {
        return true;
    }
}
