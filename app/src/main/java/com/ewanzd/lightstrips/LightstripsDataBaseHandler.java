package com.ewanzd.lightstrips;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LightstripsDataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "lightstrips.db";

    public static final String TABLE_SEQUENCES = "sequences";
    public static final String COLUMN_SEQUENCE_ID = "_id";
    public static final String COLUMN_SEQUENCE_NAME = "_name";

    public static final String TABLE_SEQUENCEITEMS = "sequenceitems";
    public static final String COLUMN_SEQUENCEITEM_ID = "_id";
    public static final String COLUMN_SEQUENCEITEM_COLOR = "_color";
    public static final String COLUMN_SEQUENCEITEM_TIME = "_time";
    public static final String COLUMN_SEQUENCEITEM_SEQUENCES_ID = "sequence_id";

    public LightstripsDataBaseHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = String.format("CREATE TABLE %1$s("+
                "%2$s INTEGER PRIMARY KEY AUTOINCREMENT," +
                "%3$s TEXT);",
                TABLE_SEQUENCES, COLUMN_SEQUENCE_ID, COLUMN_SEQUENCE_NAME);

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String query = String.format("DROP TABLE IF EXISTS %1$s;",
                TABLE_SEQUENCES);

        db.execSQL(query);
        onCreate(db);
    }

    public void addSequence(Sequence sequence) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_SEQUENCE_NAME, sequence.getName());

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_SEQUENCES, null, values);
        db.close();

        sequence.set_id(id);
    }

    public void deleteSequence(long id) {

        String query = String.format("DELETE FROM %1$s WHERE %2$s=\"%3$d\";",
                TABLE_SEQUENCES, COLUMN_SEQUENCE_ID, id);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public List<Sequence> getAllSequences() {

        List<Sequence> sequences = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("SELECT * FROM %1$s;", TABLE_SEQUENCES);

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex(COLUMN_SEQUENCE_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_SEQUENCE_NAME));

            Sequence seq = new Sequence();
            seq.set_id(id);
            seq.setName(name);
            sequences.add(seq);

            cursor.moveToNext();
        }
        cursor.close();

        db.close();
        return sequences;
    }

    public Sequence getSequenceById(long id) {

        Sequence sequence = null;
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("SELECT * FROM %1$s WHERE %2$s=\"%3$d\";",
                TABLE_SEQUENCES, COLUMN_SEQUENCE_ID, id);

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            long sequenceId = cursor.getLong(cursor.getColumnIndex(COLUMN_SEQUENCE_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_SEQUENCE_NAME));

            sequence = new Sequence();
            sequence.set_id(sequenceId);
            sequence.setName(name);
        }
        cursor.close();

        db.close();
        return sequence;
    }

    public List<SequenceItem> getSequenceItemsBySequenceId(long id) {
        return null;
    }

    public SequenceItem getSequenceItemById(long id) {
        return null;
    }

    public void addSequenceItem(SequenceItem item) {

    }

    public void deleteSequenceItem(long id) {

    }
}
