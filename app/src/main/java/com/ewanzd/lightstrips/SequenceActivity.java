package com.ewanzd.lightstrips;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ewanzd.lightstrips.core.LightstripsTimerHandler;
import com.ewanzd.lightstrips.core.RestClient;
import com.ewanzd.lightstrips.core.SiotSensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represent a sequence of colors to work with.
 */
public class SequenceActivity extends AppCompatActivity {

    // database
    private LightstripsDataBaseHandler dbHandler;

    // view
    private TextInputLayout name_layout;
    private TextInputEditText edit_name;
    private ListView lv_sequenceItems;
    private FloatingActionButton but_newSequence;
    private FloatingActionButton but_startSequence;

    // data
    private long sequenceId;
    private Sequence sequence;
    private SequenceItemAdapter adapter;

    // Extra for Intent
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
        but_startSequence = (FloatingActionButton)findViewById(R.id.but_startSequence);

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
                startSequenceItemActivity(sequence.getId(), 0);
            }
        });

        but_startSequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightHandler.run(sequence);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

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

        // sort list
        List<SequenceItem> items = sequence.getItems();
        Collections.sort(items, new Comparator<SequenceItem>() {
            @Override
            public int compare(SequenceItem item1, SequenceItem item2) {

                return item1.compareTo(item2);
            }
        });

        // refresh adapter
        adapter.clear();
        adapter.addAll(items);

        // init EditText
        edit_name.setText(sequence.getName());
    }

    @Override
    protected void onPause() {
        super.onPause();

        sequence.setName(edit_name.getText().toString());
        dbHandler.updateSequence(sequence);
    }

    @Override
    protected void onStop() {
        super.onStop();

        lightHandler.stop();
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

        RestClient client = new RestClient(this);
        SiotSensor sensor = new SiotSensor(Integer.parseInt(sensorPort), centerId, sensorId);

        lightHandler = new LightstripsTimerHandler(this, serverAddress, client, sensor);
    }

    /*protected class LightstripsTimerHandler {

        RestClient client;
        SiotSensor sensor;
        String serverAddress;
        boolean working;

        Handler handler;

        private int maxValue;
        private int currentPos;
        private int rotations;
        private List<SequenceItem> items;

        public LightstripsTimerHandler(String serverAddress, RestClient client, SiotSensor sensor) {
            this.client = client;
            this.sensor = sensor;
            this.serverAddress = serverAddress;

            handler = new Handler();
            working = false;
        }

        void run(Sequence sequence) {

            if(working) {
                Toast.makeText(SequenceActivity.this, "Wird bereits ausgeführt", Toast.LENGTH_LONG).show();
                return;
            } else if(sequence.getItems().size() == 0) {
                Toast.makeText(SequenceActivity.this, "Keine Elemente", Toast.LENGTH_LONG).show();
                return;
            }

            // init values
            working = true;
            currentPos = 0;
            rotations = 0;
            items = sequence.getItems();
            maxValue = items.get(items.size() - 1).getTime(); // must be sorted

            // start
            Toast.makeText(SequenceActivity.this, "Wurde gestartet", Toast.LENGTH_SHORT).show();
            handler.postDelayed(timerRunnable, 0);
        }

        void stop() {
            handler.removeCallbacks(timerRunnable);
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

            return String.format("{\"on\":true, \"bri\":%d, \"xy\":[%.4f,%.4f]}",
                    (int)(Y * 255), x, y);
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

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SequenceActivity.this, "Fehlgeschlagen", Toast.LENGTH_SHORT).show();
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
    }*/
}
