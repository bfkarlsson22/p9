package com.example.brand.p9;

import android.content.Context;
import android.database.Cursor;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.HashMap;
import java.util.Map;

public class DataSenderWear {
    private Context context;
    private GoogleApiClient googleApiClient;


    public DataSenderWear(Context context) {
        this.context = context;
    }


    public void syncData() {
        buildApi();

        final LocalStorageWear localStorageWear = new LocalStorageWear(context);
        Cursor dataToSend = localStorageWear.getUnsentUserData();

        if (dataToSend.moveToFirst()) {
            do {
                Long time = dataToSend.getLong(dataToSend.getColumnIndex("TIME"));
                final int id = dataToSend.getInt(dataToSend.getColumnIndex("ID"));

                PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/wear/STEP/" + id);
                putDataMapReq.getDataMap().putLong("TIME", time);
                putDataMapReq.getDataMap().putInt("ID", id);

                final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();

                Wearable.DataApi.putDataItem(googleApiClient, putDataReq).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        boolean success = dataItemResult.getStatus().isSuccess();
                        Log.d("SUCCESS", String.valueOf(success));
                        if (success) {
                            localStorageWear.updateStepData(id);
                        }
                    }
                });

            } while (dataToSend.moveToNext());
        }
    }


    public void sendMsgtoPhone(String message, String reply, String partnerUID) {
        buildApi();
        String timer = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/wear/message/" + timer);
        putDataMapReq.getDataMap().putString("message", message);
        putDataMapReq.getDataMap().putString("reply", reply);
        putDataMapReq.getDataMap().putString("partnerUID", partnerUID);
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
        Log.d("5555", message + reply);

    }

    public void buildApi() {
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        googleApiClient.connect();
    }

    public void putToLog(HashMap<String, String> logItem) {
        buildApi();
        String timer = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/wear/LOG/" + timer);

        for(Map.Entry<String, String> dataToWrite : logItem.entrySet()){
            putDataMapReq.getDataMap().putString(dataToWrite.getKey(), dataToWrite.getValue());
        }

        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
    }
}
