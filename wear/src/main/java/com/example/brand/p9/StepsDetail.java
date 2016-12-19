package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;

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
    int userGoal = 0;
    private TextView userText;
    private TextView userDetailProgress;
    private TextView partnerText;
    private TextView partnerDetailProgress;
    private ProgressBar mPartnerProgress;
    private int mPartnerProgressStatus= 0;
    String goalPartner;
    int partnerGoal = 0;




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
                mProgress = (ProgressBar) findViewById(R.id.user_progress_bar);
                userText = (TextView) findViewById(R.id.userYourProgress);
                userDetailProgress = (TextView) findViewById(R.id.userDetailText);
                mPartnerProgress = (ProgressBar) findViewById(R.id.partner_progress_bar);
                partnerText = (TextView) findViewById(R.id.PartnerYourProgress);
                partnerDetailProgress = (TextView) findViewById(R.id.partnerDetailText);

                userText.setText("Your progress");
                partnerText.setText("Your partner's progress");
                if(userGoal != 0 && partnerGoal !=0){
                    userDetailProgress.setText((int) userSteps + "out of " + userGoal);
                    partnerDetailProgress.setText((int)partnerSteps + "out of " + partnerGoal);
                }
             //   mProgress.setProgressTintList(C);
            }
        });

        Long time = System.currentTimeMillis();
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("E-d-M-y");
        String day = simpleDateFormat.format(new Date(time));
        LocalStorageWear localStorageWear = new LocalStorageWear(getApplicationContext());
        HashMap<String, String> settings = localStorageWear.getSettings();

        Cursor cursorUser = localStorageWear.getDailyData(localStorageWear.getSettings().get("UID"),"STEP",day);
        if(cursorUser.moveToFirst()){
            userSteps = cursorUser.getDouble(cursorUser.getColumnIndex("VALUE"));
        }else{
            userSteps=0;
        }

        Cursor cursorCompetitor = localStorageWear.getDailyData(localStorageWear.getSettings().get("PARTNERID"),"STEP",day);
        if(cursorCompetitor.moveToFirst()){
            partnerSteps = cursorCompetitor.getDouble(cursorCompetitor.getColumnIndex("VALUE"));
        } else {
            partnerSteps = 0;
        }

        if(settings.get("GOAL") !=null){
            userGoal = Integer.parseInt(settings.get("GOAL"));
        }else{
            userGoal = 1;
        }

        if(settings.get("PARTNERGOAL") != null) {
            partnerGoal = Integer.parseInt(settings.get("PARTNERGOAL"));
        } else {
            partnerGoal = 1;
        }

        //  goalPartner = localStorageWear.getSettings().get("PARTNERGOAL");
        //partnerGoal = Integer.parseInt(goalPartner);
        userProgress();
        partnerProgress();

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

    public void partnerProgress(){
        new Thread(new Runnable() {
            public void run() {
                while (mPartnerProgressStatus < 1000) {
                    mPartnerProgressStatus = (int) partnerSteps;

                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            mPartnerProgress.setProgress(mPartnerProgressStatus);
                        }
                    });
                }
            }
        }).start();
    }

}