package com.example.brand.p9;

import android.content.Context;
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
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseAuth fireBaseAuth = FirebaseAuth.getInstance();


    public String mMessage;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Context context = this;
        Log.d("DATA CHANGED","TRUE");
        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem dataItem = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                String path = dataItem.getUri().getPathSegments().get(1);

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
    }
    public void dataHandler(Context context, DataMap data){

        String uId = fireBaseAuth.getCurrentUser().getUid();

        DatabaseReference databaseReference = firebaseDatabase.getReference("steps/"+uId);
        String unit = data.getString("UNIT");
        double value = data.getDouble("VALUE");
        long time = data.getLong("TIME");

        HashMap<String,String> dataMap = new HashMap<>();
        dataMap.put("UNIT",unit);
        dataMap.put("VALUE", String.valueOf(value));
        dataMap.put("TIME", String.valueOf(time));

        Log.d("DATAMAP",dataMap.toString());
        databaseReference.push().setValue(dataMap);

        //SEND A CALLBACK THAT THE DATA HAS BEEN RECEIVED
        DataSenderMobile dataSenderMobile = new DataSenderMobile(context);
        dataSenderMobile.sendData();*/
    }
    public void callback(){

    }
}