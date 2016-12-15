package com.example.brand.p9;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

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
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE USERDATA (" + "ID INTEGER PRIMARY KEY AUTOINCREMENT, TIME INTEGER, SENT NUMERIC);");
        db.execSQL("CREATE TABLE DAILYDATA ("+ "ID INTEGER PRIMARY KEY, UNIT TEXT, VALUE REAL, USER TEXT, DAY TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public void addStepData(Long time){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("TIME",time);
        values.put("SENT",0);

        db.insert("USERDATA",null,values);
        db.close();
    }


    public void addToUserData(Long time, String unit, double value){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("TIME",time);
        values.put("UNIT",unit);
        values.put("VALUE",value);
        values.put("SENT",0);

        db.insert("USERDATA",null,values);
        db.close();
    }

    private void checkActiveness(Long time) {
        SQLiteDatabase db = getReadableDatabase();
        Long timeFrom = time-(16*3600000);
        String query = "SELECT SUM(VALUE) FROM USERDATA WHERE TIME >="+timeFrom+" AND UNIT='STEP'";
        Cursor cursor = db.rawQuery(query,null);
        Log.d("SUM",DatabaseUtils.dumpCursorToString(cursor));
    }

    public Cursor getUnsentUserData(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM USERDATA WHERE SENT=0";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    public Cursor getDailyData(String user, String unit, String day){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM DAILYDATA WHERE USER='"+user+"' AND UNIT='"+unit+"' AND DAY='"+day+"'";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    public boolean checkDaily(String day, String user, String unit) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT ID FROM DAILYDATA WHERE USER='" + user + "' AND DAY='" + day + "' AND UNIT='" + unit + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() < 1) {
            return false;
        } else {
            return true;
        }
    }

    public void updateDailyData(String unit, double value, String user, Long time){
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
        day = simpleDateFormat.format(new Date(time));

        if(!checkDaily(day,user,unit)){
            createDaily(day,user,unit);
        }

        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE DAILYDATA SET VALUE = VALUE + "+value+" WHERE USER='"+user+"' AND UNIT='"+unit+"' AND DAY='"+day+"'";
        db.execSQL(query);
    }

    public void update(int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE USERDATA SET SENT = 1 WHERE ID="+id;
        db.execSQL(query);
    }

    public void createDaily(String day, String user, String unit){
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("UNIT",unit);
        values.put("USER",user);
        values.put("DAY",day);
        values.put("VALUE",0);
        writableDatabase.insert("DAILYDATA", null, values);
    }


    /*
    * STORING UID AND PARTNER ID LOCALLY
     */
    public void settings(String uId, String partnerId, String userName, String partnerName, String userGoal, String partnerGoal){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("UID",uId);
        editor.putString("PARTNERID",partnerId);
        editor.putString("USERNAME",userName);
        editor.putString("PARTNERNAME",partnerName);
        editor.putString("USERGOAL",userGoal);
        editor.putString("PARTNERGOAL",partnerGoal);
        editor.apply();

    }
    public HashMap<String, String> getSettings(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return (HashMap<String, String>) preferences.getAll();
    }




}
