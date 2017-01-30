package com.ewanzd.lightstrips;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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

    private TextInputLayout time_layout;
    private TextInputEditText edit_time;
    private TextInputLayout color_layout;
    private TextInputEditText edit_color;
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
        final long sequenceItemId = intent.getLongExtra(SequenceActivity.EXTRA_SEQUENCEITEM_ID, 0);
        initState(sequenceItemId);

        // set data to view
        time_layout = (TextInputLayout)findViewById(R.id.edit_time_layout);
        edit_time = (TextInputEditText)findViewById(R.id.edit_time);
        color_layout = (TextInputLayout)findViewById(R.id.edit_color_layout);
        edit_color = (TextInputEditText)findViewById(R.id.edit_color);
        but_save = (Button)findViewById(R.id.savebutton);

        // set fields
        if(sequenceItem != null) {
            edit_time.setText(String.valueOf(sequenceItem.getTime()));
            String color = String.format("#%06X", 0xFFFFFF & sequenceItem.getColor());
            edit_color.setText(color);
        }

        but_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset
                boolean allSuccessful = true;
                time_layout.setError(null);
                color_layout.setError(null);

                // check input time
                String strtime = edit_time.getText().toString();
                if(!isInteger(strtime, 10)) {
                    time_layout.setError(getResources().getString(R.string.error_mustbenumber));
                    allSuccessful = false;
                } else {
                    int time = Integer.parseInt(edit_time.getText().toString());
                    if(time < 0) {
                        time_layout.setError(getResources().getString(R.string.error_atleastnull));
                        allSuccessful = false;
                    } else {
                        sequenceItem.setTime(time);
                    }
                }

                // check input color
                try {
                    int color = Color.parseColor(edit_color.getText().toString());
                    sequenceItem.setColor(color);
                } catch (IllegalArgumentException ex) {
                    color_layout.setError(getResources().getString(R.string.error_invalidformat));
                    allSuccessful = false;
                }

                // add to database and finish activity
                if(allSuccessful) {
                    if(sequenceItem.getId() == 0) {
                        dbHandler.addSequenceItem(sequenceId, sequenceItem);
                    } else {
                        dbHandler.updateSequenceItem(sequenceItem);
                    }
                    finishActivity();
                }
            }
        });
    }

    protected void initState(long sequenceItemId) {
        // get or create new sequence
        if(sequenceItem == null && sequenceItemId == 0) {
            sequenceItem = new SequenceItem(0, 0);
        } else if (sequenceItem == null) {
            sequenceItem = dbHandler.getSequenceItemById(sequenceItemId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void finishActivity() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(MainActivity.EXTRA_SEQUENCE_ID, sequenceId);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    // ================================ Helpers ==================================

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
