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

                String action = dataItem.getUri().getPathSegments().get(1);

                if(action.equals("SETTINGS")) {
                    settingsHandler(context, dataMap);
                } else if(action.equals("DAILYDATA")){
                    dailyData(context,dataMap);
                } else if(action.equals("message")){
                    message(dataMap);
                } else if(action.equals("data")){
                    //dataHandler(context, dataMap);
                } else if(action.equals("CALLBACK")){
                    callback(context,dataMap);
                }


            }
        }
    }

    private void dailyData(Context context, DataMap dataMap) {
        Log.d("DAILY DATA RECEIVED",dataMap.toString());
        String user = dataMap.getString("USER");
        String unit = dataMap.getString("UNIT");
        String day = dataMap.getString("DAY");
        double value = dataMap.getDouble("VALUE");

        LocalStorageWear localStorageWear = new LocalStorageWear(context);
        localStorageWear.updateDailyData(user,day,unit,value);
    }

    private void settingsHandler(Context context, DataMap dataMap) {
        LocalStorageWear localStorageWear = new LocalStorageWear(context);
        localStorageWear.settings(dataMap);
    }

    private void callback(Context context, DataMap dataMap) {
        LocalStorageWear localStorageWear = new LocalStorageWear(context);

        Log.d("DATA",dataMap.toString());
        String type = dataMap.getString("TYPE");
        int id = dataMap.getInt("ID");

        if(type.equals("STEP")){
            localStorageWear.deleteStepData(id);
        }
    }
    public void message(DataMap dataMap){
        mMessage = dataMap.get("message");
        String time = dataMap.get("time");
        String reply = dataMap.get("reply");
            Log.d("6666", mMessage + time + reply);
            Intent intent = new Intent(this, ReadReplyActivity.class);
            intent.putExtra("message", mMessage);
            intent.putExtra("time", time);
            intent.putExtra("reply", reply);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


    }
    public void dataHandler(Context context, DataMap dataMap){
        Log.d("DATA",dataMap.toString());
        String unit = dataMap.get("UNIT");
        String user = dataMap.get("USER");
        Long time = dataMap.getLong("TIME");
        double value = dataMap.getDouble("VALUE");

        LocalStorageWear localStorageWear = new LocalStorageWear(context);
        //localStorageWear.updateDailyData(unit,value,user,time);

    }


}
