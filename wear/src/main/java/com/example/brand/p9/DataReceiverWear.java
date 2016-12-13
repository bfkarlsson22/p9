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
        LocalStorageWear localStorageWear = new LocalStorageWear(this);
        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();

                Log.d("DATA ITEM",dataItem.toString());
                Log.d("DATA",dataMap.toString());
                String path = dataItem.getUri().getPathSegments().get(1);
                if(path.equals("data")){
                    Log.d("DATA RECEIVED",dataMap.toString());
                }

                mMessage = dataMap.get("message");
                String time = dataMap.get("time");
                String reply = dataMap.get("reply");
                if(mMessage !=null){
                    Log.d("6666", mMessage + time + reply);
                    Intent intent = new Intent(this, Communication.class);
                    intent.putExtra("message", mMessage);
                    intent.putExtra("time", time);
                    intent.putExtra("reply", reply);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    }



}
