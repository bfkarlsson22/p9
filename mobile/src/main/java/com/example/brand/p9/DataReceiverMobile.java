package com.example.brand.p9;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class DataReceiverMobile extends WearableListenerService {
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                Log.d("DATA MAP",dataMap.toString());
                Log.d("DATA ITEM",dataItem.toString());
                Log.d("DATA IDS", dataMap.keySet().toString());
            }
        }
        Context context = this;
        DataSenderMobile dataSenderMobile = new DataSenderMobile(context);
        dataSenderMobile.sendData();
    }
}