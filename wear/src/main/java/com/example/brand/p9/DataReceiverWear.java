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
                String time = dataMap.get("time");
                String reply = dataMap.get("reply");
                String userId = dataMap.get("userUID");
                String partnerId = dataMap.get("partnerUID");
                if(mMessage !=null){
                    Log.d("6666", mMessage + time + reply);
                    Intent intent = new Intent(this, ReadReply.class);
                    intent.putExtra("message", mMessage);
                    intent.putExtra("time", time);
                    intent.putExtra("reply", reply);
                    intent.putExtra("userUID", userId);
                    intent.putExtra("partnerUID", partnerId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    }



}
