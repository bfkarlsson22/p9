package com.example.brand.p9;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class DataReceiverWear extends WearableListenerService {

    public String mMessage;
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                mMessage = dataMap.get("message");
                if(mMessage !=null){
                    Log.d("6666", mMessage);
                    Intent intent = new Intent(this, Messages.class);
                    intent.putExtra("message", mMessage);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    }



}