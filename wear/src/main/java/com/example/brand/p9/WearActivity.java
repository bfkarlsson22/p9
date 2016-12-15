package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;

public class WearActivity extends WearableActivity {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    public SensorEventListener mStepListener;
    Context context;
    String UID;
    String partnerUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        setAmbientEnabled();
        startService(new Intent(this, DataReceiverWear.class));
        context = this;

        LocalStorageWear localStorageWear = new LocalStorageWear(context);
        UID = localStorageWear.getSettings().get("UID");
        partnerUID = localStorageWear.getSettings().get("UID"); //change to PARTNERID

        Log.d("7575", UID+partnerUID);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                setupWidgets();
                stub.setOnTouchListener(new OnSwipeTouchListener(context){
                    @Override
                    public void onSwipeLeft(){
                        Intent intent = new Intent(WearActivity.this, StepsDetail.class);
                        startActivity(intent);

                    }
                    @Override
                    public void onSwipeRight(){
                        Intent intent = new Intent(WearActivity.this, SendMsgActivity.class);
                        intent.putExtra("partnerUID", partnerUID);
                        startActivity(intent);
                    }
                });
            }


        });
        setUpStepCounter();
    }

    public void setupWidgets() {
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void setUpStepCounter(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        mStepListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == android.hardware.Sensor.TYPE_STEP_COUNTER) {
                    if (event.values.length > 0) {
                        LocalStorageWear localStorage = new LocalStorageWear(context);
                        localStorage.addToUserData(System.currentTimeMillis(),"STEP",1);

                        DataSenderWear dataSender = new DataSenderWear(context);
                        dataSender.syncData();
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        mSensorManager.registerListener(mStepListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }



}