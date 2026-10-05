package com.waiting.api_server.queue;

public class QueueKeys {
    public static String queueKey(String scheduleId) {
        return "queue:{" + scheduleId + "}";
    }
}
