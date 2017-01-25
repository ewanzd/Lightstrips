package com.ewanzd.lightstrips;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;

public class RestUnitTest {

    /*
    @Test
    public void siotRest_works() {
        int port = 19020;
        String centerGuid = "DFEB-CE02-D195-4055-A2DB-EBDA-74CA-63A2";
        String sensorGuid = "EA78-045C-A8AC-47F7-901E-7ECE-A3A7-D7D1";
        SiotSensor sensor = new SiotSensor(port, centerGuid, sensorGuid);

        try {
            SiotClientRest siotRest = new SiotClientRest();

            System.out.println("Testing 1 - Send Http GET request");
            URL urlGet = new URL(sensor.getDataUrl());
            String value = siotRest.send(urlGet);
            System.out.println(value);

            System.out.println("\nTesting 2 - Send Http POST request");
            URL urlPost = new URL("https://siot.net:12280/mqtt/request");
            //siotRest.sendPost(urlPost);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        SiotClientMqtt siotMqtt = null;
        try {
            //siotMqtt = new SiotClientMqtt();
            //siotMqtt.subscribe(sensor);

            System.out.println("Testing 3 - Send MQTT Message");
            //siotMqtt.sendMsg(sensor, "An interesting message for Mr X: Message 1");
            if(siotMqtt != null) siotMqtt.close();
        } catch (MqttException ex) {
            System.out.println(ex.getMessage());
        } finally {

        }
    }
    */
}
