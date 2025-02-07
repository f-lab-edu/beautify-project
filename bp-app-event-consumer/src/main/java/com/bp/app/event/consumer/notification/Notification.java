package com.bp.app.event.consumer.notification;

import java.util.List;
import java.util.Map;

public interface Notification {

    String KEY_TARGET_MAIL = "targetMail";
    String KEY_TARGET_CONTACT = "targetContact";
    String KEY_CONTENT = "content";
    String KEY_NOTIFICATION_TYPE = "type";

    boolean send(Map<String, ?> dataToSend);

    boolean sendAll(List<Map<String, ?>> dataListToSend);

}
