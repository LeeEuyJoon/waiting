package com.waiting.common;

public class QueueKeys {
    public static String queueKey(String scheduleId) {
        return "queue:{" + scheduleId + "}";
    }

    public static String activeKey(String scheduleId) {
        return "active:{" + scheduleId + "}";
    }
}
