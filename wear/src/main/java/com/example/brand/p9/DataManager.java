package com.example.brand.p9;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by EmilSiegenfeldt on 29/11/2016.
 */

public class DataManager {

    private final Context context;

    public DataManager(Context context){
        this.context = context;
    }

    public void sendData(){
        Log.d("CALLED","TRUE");

        SQLite sqLite = new SQLite(context);

        GoogleApiClient client = new ApiClientBuilder(context).buildClient();
        String time = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count/"+time);
        putDataMapReq.getDataMap().putInt("STEP",1);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(client,putDataReq);
    }
}
