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
    public String mUserUID;
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
                }

                mMessage = dataMap.get("message");
                String time = dataMap.get("time");
                String reply = dataMap.get("reply");
                mUserUID = dataMap.get("userUID");
                String partnerId = dataMap.get("partnerUID");
                if(mMessage !=null){
                    Log.d("6666", mMessage + time + reply);
                    Intent intent = new Intent(this, ReadReplyActivity.class);
                    intent.putExtra("message", mMessage);
                    intent.putExtra("time", time);
                    intent.putExtra("reply", reply);
                    intent.putExtra("userUID", mUserUID);
                    intent.putExtra("partnerUID", partnerId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                if(mUserUID !=null){
                    Log.d("7878", "userIdSigned:" + mUserUID);
                }
            }
        }
    }

    public String getUserUID(){
        Log.d("7878", "userIdmethod: " + mUserUID);
        return mUserUID;
    }
    public void storeSettings(Context context, DataMap dataMap){
        String UID = dataMap.getString("UID");
        String partnerId = dataMap.getString("PARTNERID");
        String partnerName = dataMap.getString("PARTNERNAME");
        String userName = dataMap.getString("USERNAME");

        LocalStorageWear localStorageWear = new LocalStorageWear(context);
        localStorageWear.settings(UID,partnerId,userName,partnerName);
    }


}
