package com.ewanzd.lightstrips;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SequenceActivity extends AppCompatActivity {

    private LightstripsDataBaseHandler dbHandler;

    private TextInputLayout name_layout;
    private TextInputEditText edit_name;
    private ListView lv_sequenceItems;
    private FloatingActionButton but_newSequence;

    private long sequenceId;
    private Sequence sequence;
    private SequenceItemAdapter adapter;

    public final static String EXTRA_SEQUENCEITEM_ID = "com.ewanzd.lightstrips.SEQUENCEITEM_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence);

        // load transfer data
        Intent intent = getIntent();
        sequenceId = intent.getLongExtra(MainActivity.EXTRA_SEQUENCE_ID, 0);

        // init Database
        dbHandler = new LightstripsDataBaseHandler(this);

        // set data to view
        edit_name = (TextInputEditText)findViewById(R.id.edit_name);
        name_layout = (TextInputLayout)findViewById(R.id.edit_name_layout);
        lv_sequenceItems = (ListView) findViewById(R.id.lv_sequenceitems);
        but_newSequence = (FloatingActionButton)findViewById(R.id.but_newSequenceItem);

        // create adapter
        adapter = new SequenceItemAdapter(this, new ArrayList<SequenceItem>());

        // init listview
        lv_sequenceItems.setAdapter(adapter);
        lv_sequenceItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                SequenceItem sequenceItem = (SequenceItem)adapter.getItemAtPosition(position);
                startSequenceItemActivity(sequence.getId(), sequenceItem.getId());
            }
        });
        lv_sequenceItems.setLongClickable(true);
        lv_sequenceItems.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv_sequenceItems.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.activity_actionmode_delete, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete:
                        SparseBooleanArray selected = adapter.getSelectedIds();
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                SequenceItem selecteditem = adapter.getItem(selected.keyAt(i));
                                dbHandler.deleteSequenceItem(selecteditem.getId());
                                adapter.remove(selecteditem);
                            }
                        }
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mode.getMenu().clear();
                adapter.removeSelection();
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                final int checkedCount = lv_sequenceItems.getCheckedItemCount();
                mode.setTitle(checkedCount + " ausgewählt");
                adapter.toggleSelection(position);
            }
        });

        // init FloatingActionButton
        but_newSequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightHandler.run(sequence);
                startSequenceItemActivity(sequence.getId(), 0);
            }
        });

        // init light handler
        initLightHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // load sequence
        if(sequenceId == 0) {
            sequence = new Sequence();
            dbHandler.addSequence(sequence);
        } else {
            sequence = dbHandler.getSequenceById(sequenceId);
        }

        // refresh adapter
        adapter.clear();
        adapter.addAll(sequence.getItems());

        // init EditText
        edit_name.setText(sequence.getName());
    }

    @Override
    protected void onPause() {
        super.onPause();

        sequence.setName(edit_name.getText().toString());
        dbHandler.updateSequence(sequence);
    }

    public void startSequenceItemActivity(long sequenceId, long sequenceItemId) {
        Intent newActivity = new Intent(SequenceActivity.this, SequenceItemActivity.class);
        newActivity.putExtra(MainActivity.EXTRA_SEQUENCE_ID, sequenceId);
        newActivity.putExtra(EXTRA_SEQUENCEITEM_ID, sequenceItemId);
        startActivityForResult(newActivity, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                sequenceId = data.getLongExtra(MainActivity.EXTRA_SEQUENCE_ID, 0);
            }
        }
    }

    // ========================== core functions timer ==============================

    LightstripsTimerHandler lightHandler;

    protected void initLightHandler() {

        String serverAddress = LightstripsConfig.getConfigValue(this, LightstripsConfig.SERVER_REST_ADDRESS);
        String sensorPort = LightstripsConfig.getConfigValue(this, LightstripsConfig.SERVER_SENSOR_PORT);
        String centerId = LightstripsConfig.getConfigValue(this, LightstripsConfig.SERVER_SENSOR_CENTERID);
        String sensorId = LightstripsConfig.getConfigValue(this, LightstripsConfig.SERVER_SENSOR_SENSORID);

        RestClient client = new RestClient();
        SiotSensor sensor = new SiotSensor(Integer.parseInt(sensorPort), centerId, sensorId);

        lightHandler = new LightstripsTimerHandler(serverAddress, client, sensor);
    }

    protected class LightstripsTimerHandler {

        RestClient client;
        SiotSensor sensor;
        String serverAddress;
        boolean working;

        Handler handler;

        public LightstripsTimerHandler(String serverAddress, RestClient client, SiotSensor sensor) {
            this.client = client;
            this.sensor = sensor;
            this.serverAddress = serverAddress;

            handler = new Handler();
            working = false;
        }

        void run(Sequence sequence) {

            client.sendGet(makeUrl("Hat alles funktioniert 2.0"));

            /*if(!working) {
                working = true;
                handler.postDelayed(timerRunnable, 0);
            } else {
                Toast.makeText(SequenceActivity.this, "Wird bereits ausgeführt", Toast.LENGTH_LONG);
            }*/
        }

        void stop() {
            handler.removeCallbacks(timerRunnable);
            working = false;
        }

        String makeUrl(String message) {
            return String.format("%1$s:%2$d/%3$s", serverAddress, sensor.getPort(), sensor.getUrlSetData(message));
        }

        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                // code
                handler.postDelayed(this, 100);
            }
        };
    }

    protected class RestClient {

        final String TAG = "RestClient";
        RequestQueue requestQueue;

        RestClient() {
            requestQueue = Volley.newRequestQueue(SequenceActivity.this);
        }

        void sendGet(String url) {

            // Request a string response
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(SequenceActivity.this, "Alles klar!", Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SequenceActivity.this, "Fehlgeschlagen", Toast.LENGTH_LONG).show();
                            error.printStackTrace();
                        }
                    }
            );
            stringRequest.setTag(TAG);

            // Add the request to the queue
            requestQueue.add(stringRequest);
        }

        void stop() {
            requestQueue.cancelAll(TAG);
        }
    }
}
