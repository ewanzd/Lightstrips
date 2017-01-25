package com.ewanzd.lightstrips;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

public class SequenceActivity extends AppCompatActivity {

    private LightstripsDataBaseHandler dbHandler;

    private EditText txe_sequenceName;
    private ListView lv_sequenceItems;

    private Sequence sequence;
    private SequenceItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence);

        // init Database
        dbHandler = new LightstripsDataBaseHandler(this, null);

        // load transfer data
        Intent intent = getIntent();
        long sequenceId = intent.getLongExtra(MainActivity.EXTRA_SEQUENCE_ID, 0);

        Log.d("seq", Long.toString(sequenceId));

        // get or create new sequence
        if(sequenceId == 0) {
            sequence = new Sequence("default");
            dbHandler.addSequence(sequence);
        } else {
            sequence = dbHandler.getSequenceById(sequenceId);
        }

        // set data to view
        txe_sequenceName = (EditText)findViewById(R.id.txe_sequencename);
        if(sequence != null) txe_sequenceName.setText(sequence.getName());

        //lv_sequenceItems = (ListView)findViewById(R.id.lv_sequenceitems);
        //adapter = new SequenceItemAdapter(this, sequence.getItems());
        //lv_sequenceItems.setAdapter(adapter);

        //ActionBar actionBar = getActionBar();
        //actionBar.setHomeButtonEnabled(true);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_sequence_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}
