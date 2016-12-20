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
import java.util.Map;

public class LocalStorageMobile extends SQLiteOpenHelper {

    private Context context;
    private static final String DB_NAME = "LocalStorage";
    private static final int DB_VERSION = 1;

    int ACTIVITY_LIMIT_MIN = 1;
    int STEPS_FOR_ACTIVENESS = 20;
    FirebaseWriter firebaseWriter = new FirebaseWriter();

    public LocalStorageMobile(Context context){
        super(context, DB_NAME,null,DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE STEPDATA (" + "ID INTEGER PRIMARY KEY, TIME INTEGER);");
        db.execSQL("CREATE TABLE DAILYDATA ("+ "ID INTEGER PRIMARY KEY, UNIT TEXT, VALUE REAL, USER TEXT, DAY TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public HashMap<String, String> getSettings(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return (HashMap<String, String>) preferences.getAll();
    }
    public void deletePartnerSettings(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("PARTNERGOAL");
        editor.remove("PARTNERNAME");
        editor.apply();
    }
    public void storeSettings(HashMap<String, String> data){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        for(Map.Entry<String, String> dataEntry : data.entrySet()){
            editor.putString(dataEntry.getKey(),dataEntry.getValue());
        }
        editor.apply();
        DataSenderMobile dataSenderMobile = new DataSenderMobile(context);
        dataSenderMobile.sendSettings(getSettings());
    }

    public void storeStepData(Long time, int id, String user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("TIME",time);
        values.put("ID",id);

        db.insert("STEPDATA",null,values);

        firebaseWriter.writeStep(user,time);
        updateDaily(user,time,"STEP");
        if(checkActiveness()){
            updateDaily(user,time,"MINUTE");
        }
    }

    public boolean checkDaily(String user, Long time, String unit){
        SQLiteDatabase db = getReadableDatabase();
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
        String day = simpleDateFormat.format(new Date(time));
        String query = "SELECT id FROM DAILYDATA WHERE USER='"+user+"' AND DAY='"+day+"' AND UNIT='"+unit+"'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.getCount() < 1){
            return false;
        } else {
            return true;
        }
    }

    public void updateDaily(String user, Long time, String unit){
        SQLiteDatabase db = getWritableDatabase();

        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
        String day = simpleDateFormat.format(new Date(time));

        if(!checkDaily(user,time,unit)){
            createDaily(user,time,unit);
        }
        String query = "UPDATE DAILYDATA SET VALUE=VALUE+1 WHERE USER='"+user+"' AND DAY='"+day+"' AND UNIT='"+unit+"'";
        db.execSQL(query);
        //Log.d("ALL DATA", DatabaseUtils.dumpCursorToString(getAllData()));
        syncDailyToWear();
        cleanDatabase();
    }

    private void syncDailyToWear() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM DAILYDATA";
        Cursor cursor = db.rawQuery(query,null);

        Log.d("DATA SYNC", DatabaseUtils.dumpCursorToString(cursor));

        if(cursor.moveToFirst()){
            do{
                String user = cursor.getString(cursor.getColumnIndex("USER"));
                String unit = cursor.getString(cursor.getColumnIndex("UNIT"));
                double value = cursor.getDouble(cursor.getColumnIndex("VALUE"));
                String day = cursor.getString(cursor.getColumnIndex("DAY"));

                DataSenderMobile dataSenderMobile = new DataSenderMobile(context);
                dataSenderMobile.sendDaily(user,unit,value,day);
            } while (cursor.moveToNext());
        }
    }

    private void createDaily(String user, Long time, String unit) {
        SQLiteDatabase db = getWritableDatabase();

        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
        String day = simpleDateFormat.format(new Date(time));

        ContentValues values = new ContentValues();

        values.put("DAY",day);
        values.put("UNIT",unit);
        values.put("USER",user);
        values.put("VALUE",0);

        db.insert("DAILYDATA",null,values);
    }

    public Cursor getAllData(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM DAILYDATA";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }
    private void cleanDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        Long time = System.currentTimeMillis();
        String timeLimit = String.valueOf(time-((1*ACTIVITY_LIMIT_MIN)*60000));
        String query = "DELETE FROM STEPDATA WHERE TIME < "+timeLimit;
        db.execSQL(query);
    }

    private boolean checkActiveness(){
        SQLiteDatabase db = getReadableDatabase();
        Long time = System.currentTimeMillis();
        String timeLimit = String.valueOf(time-(ACTIVITY_LIMIT_MIN*60000));
        String query = "SELECT * FROM STEPDATA WHERE TIME > "+timeLimit+"";
        Cursor cursor = db.rawQuery(query,null);
        Log.d("STEPS", String.valueOf(cursor.getCount()));
        if(cursor.getCount() > STEPS_FOR_ACTIVENESS){
            Log.d("ACTIVE","ACTIVE");
            SQLiteDatabase delete = getWritableDatabase();
            if(cursor.moveToFirst()){
                do {
                    delete.execSQL("DELETE FROM STEPDATA WHERE ID=" + cursor.getInt(cursor.getColumnIndex("ID")));
                } while(cursor.moveToNext());
            }
            return true;
        } else {
            int missing = STEPS_FOR_ACTIVENESS - cursor.getCount();
            Log.d("MISSING", "MISSING: "+String.valueOf(missing)+" STEPS");
            Log.d("NOT ACTIVE", "NOT ACTIVE");
            return false;
        }
    }
}

