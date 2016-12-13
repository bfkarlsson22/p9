package com.example.brand.p9;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class DataSenderWear {
    private Context context;
    private GoogleApiClient googleApiClient;


    public DataSenderWear(Context context) {
        this.context = context;
    }


    public void syncData(){
        buildApi();

        final LocalStorageWear localStorageWear = new LocalStorageWear(context);
        Cursor dataToSend = localStorageWear.getUnsentData();

        if(dataToSend.moveToFirst()){
            do {
                Long time = dataToSend.getLong(dataToSend.getColumnIndex("TIME"));
                String unit = dataToSend.getString(dataToSend.getColumnIndex("UNIT"));
                float value = dataToSend.getFloat(dataToSend.getColumnIndex("VALUE"));
                final int id = dataToSend.getInt(dataToSend.getColumnIndex("ID"));


                PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/wear/data/"+id);
                putDataMapReq.getDataMap().putLong("TIME",time);
                putDataMapReq.getDataMap().putString("UNIT",unit);
                putDataMapReq.getDataMap().putDouble("VALUE",value);
                putDataMapReq.getDataMap().putInt("ID",id);

                final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();

                Wearable.DataApi.putDataItem(googleApiClient, putDataReq).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        boolean success = dataItemResult.getStatus().isSuccess();
                        if(success){
                            localStorageWear.update(id);
                        }
                    }
                });

            } while (dataToSend.moveToNext());
        }
    }

    public void sendReply(String message, String time, String reply){
        buildApi();
        String timer = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/wear/reply/"+timer);
        putDataMapReq.getDataMap().putString("message", message);
        putDataMapReq.getDataMap().putString("time", time);
        putDataMapReq.getDataMap().putString("reply", reply);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient,putDataReq);
        Log.d("8888", message+time+reply);
    }

    public void buildApi(){
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        googleApiClient.connect();
    }
}
