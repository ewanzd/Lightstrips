package com.ewanzd.lightstrips.core;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.widget.Toast;

import com.ewanzd.lightstrips.Sequence;
import com.ewanzd.lightstrips.SequenceItem;

import java.util.List;

public class LightstripsTimerHandler {

    private RestClient client;
    private SiotSensor sensor;
    private String serverAddress;
    private boolean working;

    private Handler handler;
    private Context context;

    private int maxValue;
    private int currentPos;
    private int rotations;
    private List<SequenceItem> items;

    public LightstripsTimerHandler(Context context, String serverAddress, RestClient client, SiotSensor sensor) {
        this.context = context;
        this.client = client;
        this.sensor = sensor;
        this.serverAddress = serverAddress;

        handler = new Handler();
        working = false;
    }

    public void run(Sequence sequence) {

        if(working) {
            Toast.makeText(context, "Wird bereits ausgeführt", Toast.LENGTH_LONG).show();
            return;
        } else if(sequence.getItems().size() == 0) {
            Toast.makeText(context, "Keine Elemente", Toast.LENGTH_LONG).show();
            return;
        }

        // init values
        working = true;
        currentPos = 0;
        rotations = 0;
        items = sequence.getItems();
        maxValue = items.get(items.size() - 1).getTime(); // must be sorted

        // start
        Toast.makeText(context, "Wurde gestartet", Toast.LENGTH_SHORT).show();
        handler.postDelayed(timerRunnable, 0);
    }

    public void stop() {
        handler.removeCallbacks(timerRunnable);
        client.stop();
        working = false;
    }

    String makeUrl(String message) {
        return String.format("%1$s:%2$d/%3$s", serverAddress, sensor.getPort(), sensor.getUrlSetData(message));
    }

    String toColorxy(int color) {
        double red = Color.red(color) / 255f;
        double blue = Color.blue(color) / 255f;
        double green = Color.green(color) / 255f;

        red = (red > 0.04045f) ? Math.pow((red + 0.055f) / (1.0f + 0.055f), 2.4f) : (red / 12.92f);
        green = (green > 0.04045f) ? Math.pow((green + 0.055f) / (1.0f + 0.055f), 2.4f) : (green / 12.92f);
        blue = (blue > 0.04045f) ? Math.pow((blue + 0.055f) / (1.0f + 0.055f), 2.4f) : (blue / 12.92f);

        double X = red * 0.664511f + green * 0.154324f + blue * 0.162028f;
        double Y = red * 0.283881f + green * 0.668433f + blue * 0.047685f;
        double Z = red * 0.000088f + green * 0.072310f + blue * 0.986039f;

        double x = X / (X + Y + Z);
        double y = Y / (X + Y + Z);

        return String.format("{\"on\":true, \"bri\":%d, \"xy\":[%.4f,%.4f]}", (int)(Y * 255), x, y);
    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            SequenceItem item = items.get(currentPos);
            if(rotations++ == item.getTime()) {
                currentPos++;
                String message = toColorxy(item.getColor());
                client.sendGet(makeUrl(message));
            }

            if(rotations <= maxValue) {
                handler.postDelayed(this, 100);
            } else {
                working = false;
            }
        }
    };
}
