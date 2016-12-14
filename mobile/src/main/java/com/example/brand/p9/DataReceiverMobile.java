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
                List<String> path = dataItem.getUri().getPathSegments();
                String action = path.get(1);


                if(action.equals("data")){
                    dataHandler(dataMap, context,"UID");
                } else if(action.equals("callback")){
                    callback();
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
    public void dataHandler(DataMap data, Context context, String who){
        Log.d("DATA",data.toString());

        LocalStorageMobile localStorageMobile = new LocalStorageMobile(context);

        String dataType = data.getString("UNIT");
        DatabaseReference databaseReference;

        if(dataType.equals("STEP")){
            Log.d("DATA TYPE","STEP");
            Log.d("USER",localStorageMobile.getSettings().get("UID"));
            databaseReference = mDatabase.getReference("steps/"+localStorageMobile.getSettings().get(who));
        } else {
            Log.d("DATA TYPE","MINUTES");
            databaseReference = mDatabase.getReference("minutes/"+localStorageMobile.getSettings().get("UID"));
        }
        HashMap<String, String> dataToWrite = new HashMap<>();
        dataToWrite.put("UNIT",data.getString("UNIT"));
        dataToWrite.put("VALUE", String.valueOf(data.getDouble("VALUE")));
        dataToWrite.put("TIME", String.valueOf(data.getLong("TIME")));

        databaseReference.push().setValue(dataToWrite);
    }
    public void callback(){

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

