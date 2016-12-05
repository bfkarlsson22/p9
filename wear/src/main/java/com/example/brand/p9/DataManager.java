package com.example.brand.p9;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class DataManager {

    private final Context context;

    public DataManager(Context context){
        this.context = context;
    }

    public void sendData(DataMap dataMap){
        Log.d("CALLED","TRUE");

        GoogleApiClient client = new ApiClientBuilder(context).buildClient();
        String time = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count/"+time);
        putDataMapReq.getDataMap().putDataMap("DATA",dataMap);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(client,putDataReq);
    }
}
