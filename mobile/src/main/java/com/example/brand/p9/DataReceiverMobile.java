package com.example.brand.p9;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class DataReceiverMobile extends WearableListenerService {

    public String mMessage;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    String partnerUID;
    String userUID;


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("DATA CHANGED","TRUE");
        Context context = this;
        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                String action = dataItem.getUri().getPathSegments().get(1);

                if(action.equals("STEP")) {
                    stepHandler(dataMap, context);
                } else if(action.equals("MINUTE")){
                    minuteHandler(dataMap,context);
                } else if(action.equals("message")){
                    messageHandler(dataMap);
                } else if(action.equals("reply")){
                    replyHandler(dataMap);
                }
            }
        }
        //DataSenderMobile dataSenderMobile = new DataSenderMobile(context);
        //dataSenderMobile.sendData();
    }
    public void stepHandler(DataMap data, Context context){
        Log.d("DATA",data.toString());
    }
    public void minuteHandler(DataMap data, Context context){
        Log.d("DATA",data.toString());
    }

    public void writeToFB(String message, String reply, String partnerUID){

        Long time = System.currentTimeMillis();
        String currentTime = String.valueOf(time);
        DatabaseReference messageRef = mDatabase.getReference("messages/" +partnerUID); // change userName to partner after dev
        HashMap<String, String> messageMap = new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("time",currentTime);
        messageMap.put("reply",reply);
        messageRef.push().setValue(messageMap);
    }

    public void messageHandler(DataMap dataMap){
        mMessage = dataMap.get("message");
        String time = dataMap.get("time");
        String reply = dataMap.get("reply");
        userUID = dataMap.get("userUID");
        partnerUID = dataMap.get("partnerUID");

        if(mMessage !=null) {
            writeToFB(mMessage, reply, partnerUID);
        }

    }
    public void replyHandler(DataMap dataMap){

        }
    }

