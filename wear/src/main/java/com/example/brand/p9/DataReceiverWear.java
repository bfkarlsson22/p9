package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class DataReceiverWear extends WearableListenerService {

    public String mMessage;
    Context context;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        context = this;
        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();

                List<String> path = dataItem.getUri().getPathSegments();
                String action = path.get(1);

                if(action.equals("settings")){
                    storeSettings(context, dataMap);
                } else if(action.equals("message")){
                    message(dataMap);
                } else if(action.equals("data")){
                    dataHandler(context, dataMap);
                }


            }
        }
    }

    public void storeSettings(Context context, DataMap dataMap){

        Log.d("DATA SETTINGS",dataMap.toString());
        String UID = dataMap.getString("UID");
        String partnerId = dataMap.getString("PARTNERID");
        String partnerName = dataMap.getString("PARTNERNAME");
        String userName = dataMap.getString("USERNAME");
        String userGoal = dataMap.getString("USERGOAL");
        String partnerGoal = dataMap.get("PARTNERGOAL");

        LocalStorageWear localStorageWear = new LocalStorageWear(context);
        localStorageWear.settings(UID,partnerId,userName,partnerName,userGoal,partnerGoal);
    }
    public void message(DataMap dataMap){
        mMessage = dataMap.get("message");
        String time = dataMap.get("time");
        String reply = dataMap.get("reply");
        if(mMessage !=null){
            Log.d("6666", mMessage + time + reply);
            Intent intent = new Intent(this, ReadReplyActivity.class);
            intent.putExtra("message", mMessage);
            intent.putExtra("time", time);
            intent.putExtra("reply", reply);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
    public void dataHandler(Context context, DataMap dataMap){
        Log.d("DATA",dataMap.toString());
        String unit = dataMap.get("UNIT");
        String user = dataMap.get("USER");
        Long time = dataMap.getLong("TIME");
        double value = dataMap.getDouble("VALUE");

        LocalStorageWear localStorageWear = new LocalStorageWear(context);
        localStorageWear.updateDailyData(unit,value,user,time);

    }


}
