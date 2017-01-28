package com.ewanzd.lightstrips;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class LightstripsDataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "lightstrips.db";

    public static final String TABLE_SEQUENCE = "sequence";
    public static final String COLUMN_SEQUENCE_ID = "id";
    public static final String COLUMN_SEQUENCE_NAME = "name";

    public static final String TABLE_SEQUENCEITEM = "sequence_item";
    public static final String COLUMN_SEQUENCEITEM_ID = "id";
    public static final String COLUMN_SEQUENCEITEM_COLOR = "color";
    public static final String COLUMN_SEQUENCEITEM_TIME = "time";
    public static final String COLUMN_SEQUENCEITEM_SEQUENCES_ID = "sequence_id";

    public LightstripsDataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(String.format(
                "CREATE TABLE %1$s (" +
                "%2$s INTEGER PRIMARY KEY AUTOINCREMENT," +
                "%3$s TEXT);", // name
                TABLE_SEQUENCE, COLUMN_SEQUENCE_ID, COLUMN_SEQUENCE_NAME
        ));

        db.execSQL(String.format(
                "CREATE TABLE %1$s (" +
                "%2$s INTEGER PRIMARY KEY AUTOINCREMENT," +
                "%3$s INTEGER," + // color
                "%4$s INTEGER," + // time
                "%5$s INTEGER);", // sequence_id
                TABLE_SEQUENCEITEM, COLUMN_SEQUENCEITEM_ID, COLUMN_SEQUENCEITEM_COLOR,
                COLUMN_SEQUENCEITEM_TIME, COLUMN_SEQUENCEITEM_SEQUENCES_ID
        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(String.format(
                "DROP TABLE IF EXISTS %1$s;",
                TABLE_SEQUENCE
        ));

        db.execSQL(String.format(
                "DROP TABLE IF EXISTS %1$s;",
                TABLE_SEQUENCEITEM
        ));

        onCreate(db);
    }

    public void addSequence(Sequence sequence) {

        // create a map of values
        ContentValues values = new ContentValues();
        values.put(COLUMN_SEQUENCE_NAME, sequence.getName());

        // insert new row and returning the primary key of the new row
        SQLiteDatabase db = getWritableDatabase();
        long newId = db.insert(TABLE_SEQUENCE, null, values);
        db.close();

        sequence.setId(newId);
    }

    public void updateSequence(Sequence sequence) {

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(String.format(
                "UPDATE %1$s " +
                "SET %2$s=\"%3$s\" " +
                "WHERE %4$s=%5$d",
                TABLE_SEQUENCE, COLUMN_SEQUENCE_NAME, sequence.getName(),
                COLUMN_SEQUENCE_ID, sequence.getId()
        ));
        db.close();
    }

    public void deleteSequence(long id) {

        // delete all items from sequence
        for (SequenceItem item: getSequenceItemsBySequenceId(id)) {
            deleteSequenceItem(item.getId());
        }

        String query = String.format(
                "DELETE FROM %1$s WHERE %2$s=\"%3$d\";",
                TABLE_SEQUENCE, COLUMN_SEQUENCE_ID, id
        );

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public List<Sequence> getAllSequences() {

        List<Sequence> sequences = new ArrayList<>();
        String query = String.format(
                "SELECT * FROM %1$s;",
                TABLE_SEQUENCE
        );

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex(COLUMN_SEQUENCE_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_SEQUENCE_NAME));

            Sequence seq = new Sequence();
            seq.setId(id);
            seq.setName(name);
            sequences.add(seq);

            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        for (Sequence sequence: sequences) {
            sequence.setItems(getSequenceItemsBySequenceId(sequence.getId()));
        }

        return sequences;
    }

    public Sequence getSequenceById(long id) {

        Sequence sequence = null;
        String query = String.format(
                "SELECT * FROM %1$s WHERE %2$s=\"%3$d\";",
                TABLE_SEQUENCE, COLUMN_SEQUENCE_ID, id
        );

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            long sequenceId = cursor.getLong(cursor.getColumnIndex(COLUMN_SEQUENCE_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_SEQUENCE_NAME));

            sequence = new Sequence();
            sequence.setId(sequenceId);
            sequence.setName(name);
        }
        cursor.close();
        db.close();

        sequence.setItems(getSequenceItemsBySequenceId(sequence.getId()));
        return sequence;
    }

    public List<SequenceItem> getSequenceItemsBySequenceId(long sequenceId) {

        List<SequenceItem> sequenceItems = new ArrayList<>();
        String query = String.format(
                "SELECT * FROM %1$s WHERE %2$s=\"%3$d\"",
                TABLE_SEQUENCEITEM, COLUMN_SEQUENCEITEM_SEQUENCES_ID, sequenceId
        );

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex(COLUMN_SEQUENCEITEM_ID));
            int color = cursor.getInt(cursor.getColumnIndex(COLUMN_SEQUENCEITEM_COLOR));
            int time = cursor.getInt(cursor.getColumnIndex(COLUMN_SEQUENCEITEM_TIME));

            SequenceItem seq = new SequenceItem();
            seq.setId(id);
            seq.setColor(color);
            seq.setTime(time);
            sequenceItems.add(seq);

            cursor.moveToNext();
        }
        cursor.close();

        db.close();
        return sequenceItems;
    }

    public SequenceItem getSequenceItemById(long sequenceItemId) {

        SequenceItem sequenceItem = null;
        String query = String.format(
                "SELECT * FROM %1$s WHERE %2$s=\"%3$d\"",
                TABLE_SEQUENCEITEM, COLUMN_SEQUENCEITEM_ID, sequenceItemId
        );

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex(COLUMN_SEQUENCEITEM_ID));
            int color = cursor.getInt(cursor.getColumnIndex(COLUMN_SEQUENCEITEM_COLOR));
            int time = cursor.getInt(cursor.getColumnIndex(COLUMN_SEQUENCEITEM_TIME));

            sequenceItem = new SequenceItem();
            sequenceItem.setId(id);
            sequenceItem.setColor(color);
            sequenceItem.setTime(time);
        }
        cursor.close();

        db.close();
        return sequenceItem;
    }

    public void addSequenceItem(long sequenceId, SequenceItem item) {

        // create a map of values
        ContentValues values = new ContentValues();
        values.put(COLUMN_SEQUENCEITEM_COLOR, item.getColor());
        values.put(COLUMN_SEQUENCEITEM_TIME, item.getTime());
        values.put(COLUMN_SEQUENCEITEM_SEQUENCES_ID, sequenceId);

        // insert new row and returning the primary key of the new row
        SQLiteDatabase db = getWritableDatabase();
        long newId = db.insert(TABLE_SEQUENCEITEM, null, values);
        db.close();

        item.setId(newId);
    }

    public void deleteSequenceItem(long id) {

        String query = String.format(
                "DELETE FROM %1$s WHERE %2$s=\"%3$d\";",
                TABLE_SEQUENCEITEM, COLUMN_SEQUENCEITEM_ID, id
        );

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void updateSequenceItem(SequenceItem item) {

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(String.format(
                "UPDATE %1$s " +
                        "SET %2$s=%3$s, %4$s=%5$s " +
                        "WHERE %6$s=%7$d",
                TABLE_SEQUENCEITEM, COLUMN_SEQUENCEITEM_COLOR, item.getColor(),
                COLUMN_SEQUENCEITEM_TIME, item.getTime(), COLUMN_SEQUENCEITEM_ID, item.getId()
        ));
        db.close();
    }
}
