package com.example.brand.p9;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by EmilSiegenfeldt on 07/12/2016.
 */

public class DataSenderMobile {
    public Context context;
    public GoogleApiClient googleApiClient;

    public DataSenderMobile(Context context) {
        this.context = context;
    }

    public void sendData(String unit, String value){
        buildApi();
        String time = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/data/"+time);
        putDataMapReq.getDataMap().putString("UNIT",unit);
        putDataMapReq.getDataMap().putString("VALUE",value);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);

    }

    public void sendMessage(String message, String time, String reply, String userUID, String partnerUID){
        buildApi();
        String timer = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/message/"+timer);
        putDataMapReq.getDataMap().putString("message", message);
        putDataMapReq.getDataMap().putString("time", time);
        putDataMapReq.getDataMap().putString("reply", reply);
        putDataMapReq.getDataMap().putString("userUID", userUID);
        putDataMapReq.getDataMap().putString("partnerUID", partnerUID);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);
        Log.d("7777", message);

    }

    public void sendUserInfo(String userUID, String partnerUID){
        buildApi();
        String timer = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/message/"+timer);
        putDataMapReq.getDataMap().putString("userUID", userUID);
        putDataMapReq.getDataMap().putString("partnerUID", partnerUID);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);
        Log.d("7777", "sendUSerInfo: " +userUID+partnerUID);

    }




    public void callBack(int id){
        buildApi();
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/mobile/callback/"+id);
        putDataMapReq.getDataMap().putString("RECEIVED", String.valueOf(id));
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);
    }
    public void buildApi(){
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        googleApiClient.connect();
    }
}
