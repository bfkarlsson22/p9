package com.example.brand.p9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;

import java.util.Date;
import java.util.List;

public class LocalStorageWear extends SQLiteOpenHelper {
    private static final String DB_NAME = "LocalStorage";
    private static final int DB_VERSION = 1;
    Context context;
    String day;

    LocalStorageWear(Context context){
        super(context, DB_NAME,null,DB_VERSION);
        this.context = context;

        Long time = System.currentTimeMillis();
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
        day = simpleDateFormat.format(new Date(time));

        checkDaily();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE USERDATA (" + "ID INTEGER PRIMARY KEY AUTOINCREMENT, TIME INTEGER, UNIT TEXT, VALUE REAL, SENT NUMERIC);");
        db.execSQL("CREATE TABLE DAILYDATA ("+ "ID INTEGER PRIMARY KEY, UNIT TEXT, VALUE REAL, USER INT, DAY TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public void addUserData(Long time, String unit, double value){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("TIME",time);
        values.put("UNIT",unit);
        values.put("VALUE",value);
        values.put("SENT",0);

        db.insert("USERDATA",null,values);
        db.close();

        updateDaily(unit,day,value,0);
    }

    public void updateDaily(String unit, String day, double value, int user){
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE DAILYDATA SET VALUE = VALUE + "+value+" WHERE USER="+user+" AND UNIT='"+unit+"' AND DAY='"+day+"'";
        db.execSQL(query);
    }

    public Cursor getUnsentData(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM USERDATA WHERE SENT=0";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    public Cursor getDaily(int user, String unit, String day){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM DAILYDATA WHERE USER="+user+" AND UNIT='"+unit+"' AND DAY='"+day+"'";

        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }
    public void update(int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE USERDATA SET SENT = 1 WHERE ID="+id;
        db.execSQL(query);
    }


    //check if a daily entry exists otherwise create one
    private void checkDaily() {
        boolean createUserEntry;
        boolean createCompetitorEntry;

        SQLiteDatabase readableDatabase = getReadableDatabase();
        String queryUser = "SELECT ID FROM DAILYDATA WHERE USER=0 AND DAY='"+day+"'";
        String queryCompetitor = "SELECT ID FROM DAILYDATA WHERE USER=1 AND DAY='"+day+"'";

        Cursor cursorUser = readableDatabase.rawQuery(queryUser,null);
        Cursor cursorCompetitor = readableDatabase.rawQuery(queryCompetitor,null);

        if(cursorUser.getCount() < 1){
            createUserEntry = true;
        } else {
            createUserEntry = false;
        }

        if(cursorCompetitor.getCount() < 1){
            createCompetitorEntry = true;
        } else {
            createCompetitorEntry = false;
        }
        readableDatabase.close();

        if(createCompetitorEntry) {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("UNIT","STEP");
            values.put("USER",1);
            values.put("DAY",day);
            values.put("VALUE",0);
            writableDatabase.insert("DAILYDATA", null, values);

            values.clear();

            values.put("UNIT","MINUTES");
            values.put("USER",1);
            values.put("DAY",day);
            values.put("VALUE",0);
            writableDatabase.insert("DAILYDATA",null,values);

            writableDatabase.close();
        }

        if(createUserEntry){
            SQLiteDatabase writableDatabase = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("UNIT","STEP");
            values.put("USER",0);
            values.put("DAY",day);
            values.put("VALUE",0);
            writableDatabase.insert("DAILYDATA", null, values);

            values.clear();

            values.put("UNIT","MINUTES");
            values.put("USER",0);
            values.put("DAY",day);
            values.put("VALUE",0);
            writableDatabase.insert("DAILYDATA",null,values);

            writableDatabase.close();
        }
    }




}
