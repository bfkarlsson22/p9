package com.example.brand.p9;

import android.content.Context;
import android.database.DatabaseUtils;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DataReceiverMobile extends WearableListenerService {

    public String mMessage;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    FirebaseWriter firebaseWriter = new FirebaseWriter();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String partnerUID;
    String userUID;


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Context context = this;
        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                String action = dataItem.getUri().getPathSegments().get(1);

                if(action.equals("STEP")) {
                    stepHandler(dataMap, context);
                } else if(action.equals("message")){
                    messageHandler(dataMap);
                } else if(action.equals("reply")){
                    replyHandler(dataMap);
                }
            }
        }
    }
    public void stepHandler(DataMap data, Context context){
        if(firebaseAuth.getCurrentUser().getUid() != null){
            String UID = firebaseAuth.getCurrentUser().getUid();
            LocalStorageMobile localStorageMobile = new LocalStorageMobile(context);
            localStorageMobile.storeStepData(data.getLong("TIME"),data.getInt("ID"),UID);

            DataSenderMobile dataSenderMobile = new DataSenderMobile(context);
            dataSenderMobile.callback("STEP",data.getInt("ID"));
        }
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

