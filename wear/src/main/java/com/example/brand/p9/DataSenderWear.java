package com.example.brand.p9;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class DataSenderWear {
    private Context context;
    private GoogleApiClient googleApiClient;

    public DataSenderWear(Context context) {
        this.context = context;
    }

    public void sendData(){
        buildApi();

        LocalStorageWear localStorage = new LocalStorageWear(context);
        Cursor dataTosend = localStorage.getUnsentData();

        String timestamp = String.valueOf(System.currentTimeMillis());
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/wear/"+timestamp);

        if (dataTosend.moveToFirst()) {
            do {
                Long time = dataTosend.getLong(dataTosend.getColumnIndex("TIME"));
                String unit = dataTosend.getString(dataTosend.getColumnIndex("UNIT"));
                float value = dataTosend.getFloat(dataTosend.getColumnIndex("VALUE"));
                int id = dataTosend.getInt(dataTosend.getColumnIndex("ID"));

                DataMap dataMap = new DataMap();
                dataMap.putLong("TIME", time);
                dataMap.putString("UNIT", unit);
                dataMap.putFloat("VALUE", value);
                dataMap.putInt("ID", id);
                putDataMapReq.getDataMap().putDataMap(String.valueOf(id), dataMap);
            } while (dataTosend.moveToNext());
        }
        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
        Wearable.DataApi.putDataItem(googleApiClient, putDataReq);
    }
    public void buildApi(){
        googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        googleApiClient.connect();
    }
}
