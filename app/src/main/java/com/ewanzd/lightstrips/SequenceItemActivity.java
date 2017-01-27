package com.ewanzd.lightstrips;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SequenceItemActivity extends AppCompatActivity {

    private LightstripsDataBaseHandler dbHandler;

    private long sequenceId;
    private SequenceItem sequenceItem;

    private EditText txe_time;
    private EditText txe_color;
    private Button but_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence_item);

        // init Database
        dbHandler = new LightstripsDataBaseHandler(this);

        // load transfer data
        Intent intent = getIntent();
        sequenceId = intent.getLongExtra(MainActivity.EXTRA_SEQUENCE_ID, 0);
        long sequenceItemId = intent.getLongExtra(SequenceActivity.EXTRA_SEQUENCEITEM_ID, 0);
        initState(sequenceItemId);

        // set data to view
        txe_time = (EditText)findViewById(R.id.editTime);
        txe_color = (EditText)findViewById(R.id.editColor);
        if(sequenceItem != null) {
            txe_time.setText(String.valueOf(sequenceItem.getTime()));
            String color = String.format("#%06X", 0xFFFFFF & sequenceItem.getColor());
            txe_color.setText(color);
        }
        but_save = (Button)findViewById(R.id.saveButton);
        but_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sequenceItem.setTime(Integer.parseInt(txe_time.getText().toString()));
                sequenceItem.setColor(Color.parseColor(txe_color.getText().toString()));
                dbHandler.updateSequenceItem(sequenceItem);
            }
        });
    }

    protected void initState(long sequenceItemId) {
        // get or create new sequence
        if(sequenceItem == null && sequenceItemId == 0) {
            sequenceItem = new SequenceItem(0, 0);
            dbHandler.addSequenceItem(sequenceId, sequenceItem);
        } else if (sequenceItem == null) {
            sequenceItem = dbHandler.getSequenceItemById(sequenceItemId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                returnIntent.putExtra(MainActivity.EXTRA_SEQUENCE_ID, sequenceId);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
