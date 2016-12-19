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

import com.google.android.gms.wearable.DataMap;
import com.google.firebase.database.DataSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        db.execSQL("CREATE TABLE STEPDATA (" + "ID INTEGER PRIMARY KEY AUTOINCREMENT, TIME INTEGER, SENT NUMERIC);");
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

        db.insert("STEPDATA",null,values);

        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
        String day = simpleDateFormat.format(new Date(time));

        updateDailyData(getSettings().get("UID"),day,"STEP",1.0);
    }
    public Cursor getUnsentUserData(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM STEPDATA WHERE SENT=0";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    public Cursor getDailyData(String user, String unit, String day){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM DAILYDATA WHERE USER='"+user+"' AND UNIT='"+unit+"' AND DAY='"+day+"'";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    public void settings(DataMap settings){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();

        for(String data : settings.keySet()) {
            editor.putString(data.toString(), settings.getString(data.toString()));
        }
        editor.apply();
        Log.d("SETTINGS WEAR",getSettings().toString());

    }
    public HashMap<String, String> getSettings(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return (HashMap<String, String>) preferences.getAll();
    }
    public void updateStepData(int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE STEPDATA SET SENT = 1 WHERE ID="+id;
        db.execSQL(query);
    }
    public void deleteStepData(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM STEPDATA WHERE ID="+id;
        db.execSQL(query);
    }

    public void updateDailyData(String user, String day, String unit, Double value){
        if(user != null) {
            SQLiteDatabase db = getWritableDatabase();
            String query;

            if (!checkDailyData(user, day, unit)) {
                createDailyData(user, day, unit);
            }

            if (value != 0) {
                query = "UPDATE DAILYDATA SET VALUE=" + value + " WHERE USER='" + user + "' AND DAY='" + day + "' AND UNIT='" + unit + "'";
            } else {
                query = "UPDATE DAILYDATA SET VALUE=VALUE+1 WHERE USER='" + user + "' AND DAY='" + day + "' AND UNIT='" + unit + "'";
            }
            db.execSQL(query);
        }
    }
    private void createDailyData(String user, String day, String unit) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("DAY",day);
        values.put("UNIT",unit);
        values.put("USER",user);
        values.put("VALUE",0);

        db.insert("DAILYDATA",null,values);
    }
    public boolean checkDailyData(String user, String day, String unit){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT ID FROM DAILYDATA WHERE USER='"+user+"' AND DAY='"+day+"' AND UNIT='"+unit+"'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() < 1){
            return false;
        } else {
            return true;
        }

    }
}
