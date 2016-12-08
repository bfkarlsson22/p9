package com.example.brand.p9;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class DataReceiverWear extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = event.getDataItem();
                Log.d("DATA ITEM",dataItem.toString());
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                Log.d("DATA MAP",dataMap.toString());

            }
        }
    }
}
