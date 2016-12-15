package com.example.brand.p9;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class LocalStorageMobile {

    private Context context;

    public LocalStorageMobile(Context context){
        this.context = context;
    }

    public HashMap<String, String> getSettings(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return (HashMap<String, String>) preferences.getAll();
    }
    public void deleteSettings(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
    public void deletePartnerSettings(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("PARTNERGOAL");
        editor.remove("PARTNERNAME");
        editor.apply();
    }
    public void storeData(HashMap<String, String> data){
        Log.d("DATA STORAGE",data.toString());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        for(Map.Entry<String, String> dataEntry : data.entrySet()){
            editor.putString(dataEntry.getKey(),dataEntry.getValue());
        }
        editor.apply();
        Log.d("SETTINGS",getSettings().toString());
    }

}

