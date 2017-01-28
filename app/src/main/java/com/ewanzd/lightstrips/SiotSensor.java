package com.ewanzd.lightstrips;

import java.util.regex.Pattern;

public class SiotSensor {
    private static final Pattern pattern;
    private final int port;
    private final String centerUid;
    private final String sensorUid;

    static {
        pattern = Pattern.compile("([0-9A-F]{4}-){7}[0-9A-F]{4}");
    }

    public SiotSensor(int port, String centerUid, String sensorUid) throws IllegalArgumentException {
        if(!portIsValid(port)) {
            throw new IllegalArgumentException("Invalid port");
        } else if(!uidIsValid(centerUid)) {
            throw new IllegalArgumentException("Invalid centerUid");
        } else if(!uidIsValid(sensorUid)) {
            throw new IllegalArgumentException("Invalid sensorUid");
        }

        this.port = port; this.centerUid = centerUid; this.sensorUid = sensorUid;
    }

    public String getUrlGetData() {
        return String.format("getdata?centerUID=%s&sensorUID=%s",
                getCenterUid(), getSensorUid());
    }

    public String getUrlSetData(String message) {
        return String.format("mqtt/request?topic=%s&message=%s",
                getBody(MqttType.DAT), message);
    }

    public String getBody(MqttType type) {
        return String.format("siot/%s/%s/%s",
                type.toString(), getCenterUid(), getSensorUid());
    }

    public static boolean uidIsValid(String uid) {
        return pattern.matcher(uid).matches();
    }

    public static boolean portIsValid(int port) {
        return (port >= 0 && port <= 65535);
    }

    public int getPort() {
        return port;
    }

    public String getCenterUid() {
        return centerUid;
    }

    public String getSensorUid() {
        return sensorUid;
    }

    public enum MqttType {
        MNF,    // PUBLISH MANIFEST
        CNF,    // PUBLISH CONFIGURATION
        DAT,    // PUBLISH DATA
        STA,    // PUBLISH STATUS
        CMD     // PUBLISH COMMAND
    }
}
