package com.ewanzd.lightstrips;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;

public class SequenceActivity extends AppCompatActivity {

    private LightstripsDataBaseHandler dbHandler;

    private EditText txe_sequenceName;
    private ListView lv_sequenceItems;
    private FloatingActionButton but_newSequence;

    private Sequence sequence;
    private SequenceItemAdapter adapter;

    public final static String EXTRA_SEQUENCEITEM_ID = "com.ewanzd.lightstrips.SEQUENCEITEM_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence);

        // init Database
        dbHandler = new LightstripsDataBaseHandler(this);

        // load transfer data
        Intent intent = getIntent();
        long sequenceId = intent.getLongExtra(MainActivity.EXTRA_SEQUENCE_ID, 0);
        initState(sequenceId);

        // set sequences to adapter
        adapter = new SequenceItemAdapter(this, sequence.getItems());

        // set data to view
        txe_sequenceName = (EditText)findViewById(R.id.txe_sequencename);
        if(sequence != null) txe_sequenceName.setText(sequence.getName());

        // init listview
        lv_sequenceItems = (ListView) findViewById(R.id.lv_sequenceitems);
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
                mode.setTitle(checkedCount + " Selected");
                adapter.toggleSelection(position);
            }
        });

        // init FloatingActionButton
        but_newSequence = (FloatingActionButton)findViewById(R.id.but_newSequenceItem);
        but_newSequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSequenceItemActivity(sequence.getId(), 0);
            }
        });
    }

    protected void initState(long sequenceId) {
        // get or create new sequence
        if(sequence == null && sequenceId == 0) {
            sequence = new Sequence("default");
            dbHandler.addSequence(sequence);
        } else if (sequence == null) {
            sequence = dbHandler.getSequenceById(sequenceId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        sequence.setName(txe_sequenceName.getText().toString());
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
                final long result = data.getLongExtra(MainActivity.EXTRA_SEQUENCE_ID, 0);
                initState(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                initState(0);
            }
        }
    }
}
