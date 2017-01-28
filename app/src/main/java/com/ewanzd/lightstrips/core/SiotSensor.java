package com.ewanzd.lightstrips.core;

import java.util.regex.Pattern;

/**
 * Represent a sensor from SIOT plattform. Object is immutable.
 * https://siot.me/
 */
public class SiotSensor {
    private static final Pattern pattern;
    private final int port;
    private final String centerUid;
    private final String sensorUid;

    static {
        pattern = Pattern.compile("([0-9A-F]{4}-){7}[0-9A-F]{4}");
    }

    /**
     * Create a new immutable SIOT sensor. Arguments will be checked wether they are valid.
     * @param port Port of sensor.
     * @param centerUid GUID from center.
     * @param sensorUid GUID from sensor.
     * @throws IllegalArgumentException Some arguments have no correct format.
     */
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

    /**
     * Get url for get last entry from SIOT sensor.
     * @return URL for REST.
     */
    public String getUrlGetData() {
        return String.format("getdata?centerUID=%s&sensorUID=%s",
                getCenterUid(), getSensorUid());
    }

    /**
     * Get url for put message to SIOT sensor.
     * @param message Message to send.
     * @return URL for REST.
     */
    public String getUrlSetData(String message) {
        return String.format("mqtt/request?topic=%s&message=%s",
                getBody(MqttType.DAT), message);
    }

    /**
     * Body format for MQTT functions for connect to SIOT.
     * @param type Kind of data.
     * @return Common MQTT body format for connect to SIOT.
     */
    public String getBody(MqttType type) {
        return String.format("siot/%s/%s/%s",
                type.toString(), getCenterUid(), getSensorUid());
    }

    /**
     * Check GUID is valid for SIOT.
     * @param uid GUID to check.
     * @return True = valid, false = invalid.
     */
    public static boolean uidIsValid(String uid) {
        return pattern.matcher(uid).matches();
    }

    /**
     * Check port is valid.
     * @param port Port to check.
     * @return True = valid, false = invalid.
     */
    public static boolean portIsValid(int port) {
        return (port >= 0 && port <= 65535);
    }

    /**
     * Get port of sensor.
     * @return Port of sensor.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get center GUID of sensor.
     * @return Center GUID of sensor.
     */
    public String getCenterUid() {
        return centerUid;
    }

    /**
     * Get sensor GUID.
     * @return Sensor GUID.
     */
    public String getSensorUid() {
        return sensorUid;
    }

    /**
     * Kind of data to send to SIOT.
     */
    public enum MqttType {
        MNF,    // PUBLISH MANIFEST
        CNF,    // PUBLISH CONFIGURATION
        DAT,    // PUBLISH DATA
        STA,    // PUBLISH STATUS
        CMD     // PUBLISH COMMAND
    }
}
