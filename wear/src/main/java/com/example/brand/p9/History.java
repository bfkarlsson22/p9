package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by brand on 12/19/2016.
 */
public class History extends WearableActivity implements WearableListView.ClickListener {
    private WearableListView listView;

    ArrayList<String> dayList = new ArrayList<String>();
    List<SettingsItems> items;
    LocalStorageWear localStorageWear;
    String userUID;
    String partnerUID;
    HashMap<String, String> userHash;
    HashMap<String, String> partnerHash;
    int stepsUser;
    int stepsPartner;
    HashMap<String, String> settings;
    int userGoal;
    int partnerGoal;
    float userGoalPercentage;
    float partnerGoalPercentage;
    SettingsAdapter mAdapter;
    String userName;
    String partnerName;
    Context mContext =this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                listView = (WearableListView) stub.findViewById(R.id.sample_list_view);
                loadAdapter();
                localStorageWear = new LocalStorageWear(mContext);
                userHash = new HashMap<>();
                partnerHash = new HashMap<>();
                settings = localStorageWear.getSettings();
                userName = localStorageWear.getSettings().get("NAME");
                partnerName = localStorageWear.getSettings().get("PARTNERNAME");
                userUID = localStorageWear.getSettings().get("UID");
                partnerUID = localStorageWear.getSettings().get("PARTNER");
                setDates();
                setGoals();
                getInfo();
                calcWinner();

                stub.setOnTouchListener(new OnSwipeTouchListener(mContext) {
                    @Override
                    public void onSwipeLeft() {

                    }

                    @Override
                    public void onSwipeRight() {
                        //Intent intent = new Intent(History.this, DetailActivity.class);
                        //startActivity(intent);
                        onBackPressed();
                    }
                });

            }


        });
    }

    public void setDates(){
        Long time = System.currentTimeMillis();
        Long time2 = System.currentTimeMillis() - 86400000;
        Long time3 = System.currentTimeMillis() - (2*86400000);
        Long time4 = System.currentTimeMillis() - (3*86400000);
        Long time5 = System.currentTimeMillis() - (4*86400000);
        Long time6 = System.currentTimeMillis() - (5*86400000);
        java.text.SimpleDateFormat simpleDateFormat1 = new java.text.SimpleDateFormat("E-d-M-y");
        String dbDay = simpleDateFormat1.format(new Date(time));
        String dbDay2 = simpleDateFormat1.format(new Date(time2));
        String dbDay3 =simpleDateFormat1.format(new Date(time3));
        String dbDay4 = simpleDateFormat1.format(new Date(time4));
        String dbDay5 = simpleDateFormat1.format(new Date(time5));
        String dbDay6 = simpleDateFormat1.format(new Date(time6));
        dayList.add(dbDay);
        dayList.add(dbDay2);
        dayList.add(dbDay3);
        dayList.add(dbDay4);
        dayList.add(dbDay5);
        dayList.add(dbDay6);


    }

    public void setGoals(){
        if(settings.get("GOAL") !=null){
            userGoal = Integer.parseInt(settings.get("GOAL"));
            Log.d("7979 usergoal", String.valueOf(userGoal));
        }else{
            userGoal = 1;
        }

        if(settings.get("PARTNERGOAL") != null) {
            partnerGoal = Integer.parseInt(settings.get("PARTNERGOAL"));
            Log.d("7979 partnergoal", String.valueOf(partnerGoal));

        } else {
            partnerGoal = 1;
        }
    }

    public void getInfo(){
        for(String d : dayList){
            Cursor cursorUser = localStorageWear.getAllSteps(d, userUID);
            if(cursorUser.moveToFirst()){
                do{
                    String steps = cursorUser.getString(cursorUser.getColumnIndex("VALUE"));
                    String day = cursorUser.getString(cursorUser.getColumnIndex("DAY"));
                    Log.d("9898", String.valueOf(steps+day));
                    userHash.put(day, steps);


                } while(cursorUser.moveToNext());
            }
            Cursor cursorPartner = localStorageWear.getAllSteps(d, partnerUID);
            if(cursorPartner.moveToFirst()){
                do{
                    String steps = cursorPartner.getString(cursorPartner.getColumnIndex("VALUE"));
                    String day = cursorPartner.getString(cursorPartner.getColumnIndex("DAY"));
                    Log.d("9898", String.valueOf(steps+day));
                    partnerHash.put(day, steps);

                } while(cursorPartner.moveToNext());
            }
        }
    }

    public void calcWinner(){

        for(String d : dayList){

            String userSteps = userHash.get(d);
            if(userSteps != null) {
                stepsUser = Integer.parseInt(userSteps);
                userGoalPercentage =  (stepsUser*1000/userGoal);
            }

            String partnerSteps = partnerHash.get(d);
            if (partnerSteps != null){
                stepsPartner = Integer.parseInt(partnerSteps);
                partnerGoalPercentage = (stepsPartner*1000/partnerGoal);
            }

            if(userSteps !=null && partnerSteps !=null){
                if(userGoalPercentage>partnerGoalPercentage){
                    items.add(new SettingsItems(d, userName));
                }else{
                    items.add(new SettingsItems(d,partnerName));


                }
            }
        }

    }


    private void loadAdapter() {
        items = new ArrayList<>();



        mAdapter = new SettingsAdapter(this, items);


        listView.setAdapter(mAdapter);

        listView.setClickListener(this);

    }
    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Log.d("POSITION", String.valueOf(viewHolder.getPosition()));
        Intent intent = new Intent(History.this,DetailActivity.class);
        intent.putExtra("DAY",items.get(viewHolder.getPosition()).day);
        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }
}
