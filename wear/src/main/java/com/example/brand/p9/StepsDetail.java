package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by brand on 12/15/2016.
 */

public class StepsDetail extends WearableActivity {

    private Context mContext = this;
    private static final int PROGRESS = 0x1;

    private ProgressBar mProgress;
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();
    private double userSteps;
    private double partnerSteps;
    String goal;
    int userGoal;
    private TextView userText;
    private ProgressBar mPartnerProgress;
    private int mPartnerProgressStatus= 0;
    String goalPartner;
    int partnerGoal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stepsdetail);
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                stub.setOnTouchListener(new OnSwipeTouchListener(mContext){
                    @Override
                    public void onSwipeLeft(){


                    }
                    @Override
                    public void onSwipeRight(){
                        Intent intent = new Intent(StepsDetail.this, WearActivity.class);
                        startActivity(intent);
                    }
                });
                mProgress = (ProgressBar) findViewById(R.id.progress_bar);
                userText = (TextView) findViewById(R.id.usersteptext);
                userText.setText("Your progress");
            }
        });

        Long time = System.currentTimeMillis();
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
        String day = simpleDateFormat.format(new Date(time));
        LocalStorageWear localStorageWear = new LocalStorageWear(getApplicationContext());
        Cursor cursorUser = localStorageWear.getDailyData(localStorageWear.getSettings().get("UID"),"STEP",day);
        if(cursorUser.moveToFirst()){
            userSteps = cursorUser.getDouble(cursorUser.getColumnIndex("VALUE"));

        }else{
            userSteps=0;
        }
        goal = localStorageWear.getSettings().get("USERGOAL");
        Log.d("8989", String.valueOf(userSteps+" goal: "+goal));
        userGoal = Integer.parseInt(goal);

        Cursor cursorCompetitor = localStorageWear.getDailyData(localStorageWear.getSettings().get("PARTNERID"),"STEP",day);
        if(cursorCompetitor.moveToFirst()){
            partnerSteps = cursorCompetitor.getDouble(cursorCompetitor.getColumnIndex("VALUE"));
        } else {
            partnerSteps = 0;
        }
        goalPartner = localStorageWear.getSettings().get("PARTNERGOAL");
        partnerGoal = Integer.parseInt(goalPartner);
        userProgress();

    }

    public void userProgress(){
        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus < 1000) {
                    mProgressStatus = (int) userSteps;

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                        }
                    });
                }
            }
        }).start();
    }

}
