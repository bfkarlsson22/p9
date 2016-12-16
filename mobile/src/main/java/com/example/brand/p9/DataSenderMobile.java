package com.example.brand.p9;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by EmilSiegenfeldt on 07/12/2016.
 */

public class DataSenderMobile {
    public Context context;
    public GoogleApiClient googleApiClient;

    public DataSenderMobile(Context context) {
        this.context = context;
    }

    public void sendMessage(String message, String time, String reply) {
        buildApi();
        String timer = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/message/" + timer);
        putDataMapReq.getDataMap().putString("message", message);
        putDataMapReq.getDataMap().putString("time", time);
        putDataMapReq.getDataMap().putString("reply", reply);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
        Log.d("7777", message);

    }
    public void buildApi(){
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        googleApiClient.connect();
    }

    //NEW METHODS
    public void callback(String type, int id) {

        buildApi();
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/CALLBACK/");
        putDataMapReq.getDataMap().putInt("ID",id);
        putDataMapReq.getDataMap().putString("TYPE", type);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);

    }
    public void sendSettings(HashMap<String, String> settings) {
        buildApi();

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/SETTINGS/");

        for(Map.Entry<String, String> dataToSend : settings.entrySet()){
            putDataMapReq.getDataMap().putString(dataToSend.getKey(), dataToSend.getValue());
        }

        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);

    }
    public void sendDaily(String user, String unit, double value, String day){
        buildApi();
        Long time = System.currentTimeMillis();
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/DAILYDATA/"+time);

        putDataMapReq.getDataMap().putString("USER", user);
        putDataMapReq.getDataMap().putString("UNIT", unit);
        putDataMapReq.getDataMap().putDouble("VALUE", value);
        putDataMapReq.getDataMap().putString("DAY",day);

        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient, putDataReq);

    }
}
