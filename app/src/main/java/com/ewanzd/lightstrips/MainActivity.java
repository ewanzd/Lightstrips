package com.ewanzd.lightstrips;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
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
        dbHandler = new LightstripsDataBaseHandler(this);

        // set sequences to adapter
        sequences = new ArrayList<>();
        adapter = new SequenceAdapter(this, sequences);

        // init ListView
        lv_sequence = (ListView) findViewById(R.id.listview_sequences);
        lv_sequence.setAdapter(adapter);
        lv_sequence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                Sequence sequence = (Sequence)adapter.getItemAtPosition(position);
                startSequenceActivity(sequence.getId());
            }
        });
        lv_sequence.setLongClickable(true);
        lv_sequence.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv_sequence.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
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
                        for (int i = 0; i < selected.size(); i++) {
                            if (selected.valueAt(i)) {
                                Sequence selecteditem = adapter.getItem(selected.keyAt(i));
                                dbHandler.deleteSequence(selecteditem.getId());
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

                final int checkedCount = lv_sequence.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                adapter.toggleSelection(position);
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
    protected void onResume() {
        super.onResume();

        reloadSequences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_actionmode_delete, menu);

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

    /**
     *
     * @param sequenceId
     */
    public void startSequenceActivity(long sequenceId) {
        Intent newActivity = new Intent(MainActivity.this, SequenceActivity.class);
        newActivity.putExtra(EXTRA_SEQUENCE_ID, sequenceId);
        startActivity(newActivity);
    }
}
