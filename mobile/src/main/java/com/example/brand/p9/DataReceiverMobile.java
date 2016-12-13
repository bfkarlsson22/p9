package com.example.brand.p9;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class DataReceiverMobile extends WearableListenerService {

    public String mMessage;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("DATA CHANGED","TRUE");
        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                List<String> path = dataItem.getUri().getPathSegments();
                String action = path.get(1);

                mMessage = dataMap.get("message");
                String time = dataMap.get("time");
                String reply = dataMap.get("reply");
                Log.d("6666", "reply: "+reply);
                if(mMessage !=null){
                    Log.d("4444", mMessage + time + reply);
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("message", mMessage);
                    intent.putExtra("time", time);
                    intent.putExtra("reply", reply);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                if(action.equals("data")){
                    dataHandler(dataMap);
                } else if(action.equals("callback")){
                    callback();
                }
            }
        }
/*        Context context = this;
        DataSenderMobile dataSenderMobile = new DataSenderMobile(context);
        dataSenderMobile.sendData();*/
    }
    public void dataHandler(DataMap data){
        Log.d("DATA",data.toString());
    }
    public void callback(){

    }
}