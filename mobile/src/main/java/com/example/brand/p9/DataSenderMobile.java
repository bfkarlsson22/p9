package com.example.brand.p9;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by EmilSiegenfeldt on 07/12/2016.
 */

public class DataSenderMobile {
    public Context context;
    public GoogleApiClient googleApiClient;

    public DataSenderMobile(Context context) {
        this.context = context;
    }

    public void sendData(String unit, double value, Long time, String user){

        buildApi();
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/data");
        putDataMapReq.getDataMap().putString("UNIT",unit);
        putDataMapReq.getDataMap().putDouble("VALUE",value);
        putDataMapReq.getDataMap().putLong("TIME",time);
        putDataMapReq.getDataMap().putString("USER",user);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);

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

    public void sendSettings(String uId, String partnerId, String partnerName, String userName, String userGoal, String partnerGoal){
        buildApi();
        String time = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/settings/"+time);
        putDataMapReq.getDataMap().putString("UID", uId);
        putDataMapReq.getDataMap().putString("PARTNERID", partnerId);
        putDataMapReq.getDataMap().putString("PARTNERNAME", partnerName);
        putDataMapReq.getDataMap().putString("USERNAME", userName);
        putDataMapReq.getDataMap().putString("USERGOAL",userGoal);
        putDataMapReq.getDataMap().putString("PARTNERGOAL",partnerGoal);

        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);
    }

    public void buildApi(){
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        googleApiClient.connect();
    }
}
