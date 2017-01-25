package com.ewanzd.lightstrips;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private LightstripsDataBaseHandler dbHandler;

    private SequenceAdapter adapter;
    private List<Sequence> sequences;

    private ListView lv_sequence;
    private FloatingActionButton but_newSequence;

    public final static String EXTRA_SEQUENCE_ID = "com.ewanzd.lightstrips.SEQUENCE_ID";

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init Database
        dbHandler = new LightstripsDataBaseHandler(this, null);

        // load sequences
        sequences = dbHandler.getAllSequences();

        // set sequences to adapter
        adapter = new SequenceAdapter(this, sequences);

        // init ListView
        lv_sequence = (ListView) findViewById(R.id.listview_sequences);
        lv_sequence.setAdapter(adapter);
        lv_sequence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Sequence sequence = (Sequence)adapter.getItemAtPosition(position);

                startSequenceActivity(sequence.get_id());
            }
        });

        // init FloatingActionButton
        but_newSequence = (FloatingActionButton)findViewById(R.id.but_newSequence);
        but_newSequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSequenceActivity(0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     *
     */
    public void reloadSequences() {
        // load sequences
        sequences = dbHandler.getAllSequences();

        // refresh adapter
        adapter.clear();
        adapter.addAll(sequences);
    }

    public void startSequenceActivity(long sequenceId) {
        Intent newActivity = new Intent(MainActivity.this, SequenceActivity.class);
        newActivity.putExtra(EXTRA_SEQUENCE_ID, sequenceId);
        startActivity(newActivity);
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        String address = getConfigValue(this, "server_mqtt_address");
        String port = getConfigValue(this, "server_mqtt_port");
        String serverAddress = String.format("%1$s:%2$s", address, port);

        SiotSensor sensor = new SiotSensor(
                Integer.getInteger(getConfigValue(this, "sensor_test_port")),
                getConfigValue(this, "sensor_test_centerId"),
                getConfigValue(this, "sensor_test_sensorId"));

        try {
            siotClientMqtt = new SiotClientMqtt(this, serverAddress);
            siotClientMqtt.connect();
            siotClientMqtt.subscribe(sensor);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            siotClientMqtt.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }*/
}
