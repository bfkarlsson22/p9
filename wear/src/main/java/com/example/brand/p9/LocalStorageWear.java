package com.example.brand.p9;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.wearable.DataMap;

import java.util.List;

public class LocalStorageWear extends SQLiteOpenHelper {
    private static final String DB_NAME = "LocalStorage";
    private static final int DB_VERSION = 1;
    Context context;

    LocalStorageWear(Context context){
        super(context, DB_NAME,null,DB_VERSION);
        this.context = context;
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
    }

    public void addDailyData(String unit, double value, String day, String user){

    }


    /*public void updateDaily(String unit, double value, String day){
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE DAILYDATA SET "+unit+" = "+value+" WHERE USER = 1 AND DAY = "+day;
        db.execSQL(query);
    }*/

    public Cursor getUnsentData(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM USERDATA WHERE SENT=0";

        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }
}
