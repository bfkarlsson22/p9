package com.example.brand.p9;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by EmilSiegenfeldt on 14/12/2016.
 */

public class LocalStorageMobile {

    private Context context;

    public LocalStorageMobile(Context context){
        this.context = context;
    }

    public void storeSettings(String uId, String partnerId, String userName, String partnerName, String userGoal, String partnerGoal, LocalStorageInterface callback){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("UID",uId);
        editor.putString("PARTNERID",partnerId);
        editor.putString("USERNAME",userName);
        editor.putString("PARTNERNAME",partnerName);
        editor.putString("USERGOAL",userGoal);
        editor.putString("PARTNERGOAL",partnerGoal);

        editor.apply();
        Log.d("SETTING",getSettings().toString());
        callback.onStorageDone();

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

}

