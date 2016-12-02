package com.example.brand.p9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by EmilSiegenfeldt on 01/12/2016.
 */

public class SQLite extends SQLiteOpenHelper {
    private static final String DB_NAME = "LocalStorage";
    private static final int DB_VERSION = 1;

    SQLite(Context context){
        super(context, DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE DATA (" + "TIME INTEGER PRIMARY KEY, UNIT TEXT, VALUE REAL, SENT NUMERIC);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public void addData(long time, String unit, double value){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("TIME", time);
        values.put("UNIT",unit);
        values.put("VALUE",value);
        values.put("SENT",0);

        db.insert("DATA",null,values);
        db.close();
    }

    public Cursor getData(){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM DATA ORDER BY TIME DESC";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    public void syncData(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM DATA WHERE SENT='0' ORDER BY TIME DESC";
        Cursor cursor = db.rawQuery(query,null);


    }
}
