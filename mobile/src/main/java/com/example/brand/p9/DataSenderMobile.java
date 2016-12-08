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
    private Context context;
    private GoogleApiClient googleApiClient;

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
    public void buildApi(){
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        googleApiClient.connect();
    }
}
