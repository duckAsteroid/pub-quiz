package com.asteroid.duck.pubquiz.rest.socket;

/**
 * Helper for managing channel names etc.
 */
public class Channel {
    public final static String ROOT = "/quiz";
    public static final String WEBSOCKET_PATH = "/quiz-websocket";
    /** Prefix for all inbound messages */
    public static final String SERVER_PREFIX = "/server";
    /** Prefix for all outbound messages */
    public static final String CLIENT_PREFIX = "/client";

    public static String session(String sessionId) {
        return CLIENT_PREFIX + "/sessions/" + sessionId;
    }

    public static String teams(String sessionShortId) {
        return session(sessionShortId) + "/teams";
    }
}
