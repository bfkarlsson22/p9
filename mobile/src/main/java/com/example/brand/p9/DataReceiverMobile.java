package com.example.brand.p9;

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
        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                List<String> path = dataItem.getUri().getPathSegments();
                String action = path.get(1);

                mMessage = dataMap.get("message");
                String time = dataMap.get("time");
                String reply = dataMap.get("reply");
                userUID = dataMap.get("userUID");
                partnerUID = dataMap.get("partnerUID");

                String sendMessage = dataMap.get("sendMessage");
                String sendTime = dataMap.get("sendTime");
                String sendReply = dataMap.get("sendReply");
                Log.d("6666", "reply: "+reply);

                if(mMessage !=null){
                    writeToFB(mMessage, reply, userUID); // change to partnerID after dev
                }

                if(sendMessage !=null){
                    Log.d("4545", sendMessage+sendTime+sendReply);
                    writeToFB(sendMessage, sendReply, userUID);
                }

                if(action.equals("data")){
                    dataHandler(dataMap);
                } else if(action.equals("callback")){
                    callback();
                } else if(action.equals("message")){
                    messageHandler(dataMap);
                } else if(action.equals("reply")){
                    replyHandler(dataMap);
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

    }
    public void replyHandler(DataMap dataMap){

    }

}