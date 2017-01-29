package com.ewanzd.lightstrips;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Helper to get config data from file.
 */
public class LightstripsConfig {

    private static final String TAG = "ConfigHelper";

    public static final String SERVER_REST_ADDRESS = "server_rest_address";
    public static final String SERVER_SENSOR_PORT = "server_sensor_port";
    public static final String SERVER_SENSOR_CENTERID = "server_sensor_centerId";
    public static final String SERVER_SENSOR_SENSORID = "server_sensor_sensorId";

    /**
     * Get config data.
     * @param context Context of caller.
     * @param key Key of the specific config data.
     * @return
     */
    public static String getConfigValue(Context context, String key) {

        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(key);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

        return null;
    }
}
