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

    public void sendData(){
        buildApi();
        String time = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/"+time);
        putDataMapReq.getDataMap().putString("RECEIVED AT MOBILE","RECEIVED AT MOBILE");
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);

    }

    public void sendMessage(String message, String time, String reply){
        buildApi();
        String timer = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/message/"+timer);
        putDataMapReq.getDataMap().putString("message", message);
        putDataMapReq.getDataMap().putString("time", time);
        putDataMapReq.getDataMap().putString("reply", reply);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);
        Log.d("7777", message);

    }


    public void callBack(String[] ids){
        buildApi();
        String time = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/callback");
        putDataMapReq.getDataMap().putStringArray("Received data ids",ids);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);
    }
    public void buildApi(){
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        googleApiClient.connect();
    }
}
