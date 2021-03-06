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
import android.widget.Button;

public class WearActivity extends WearableActivity {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    public SensorEventListener mStepListener;
    LocalStorageWear localStorageWear;
    DataSenderWear dataSenderWear;
    Context context;
    String UID;
    String partnerUID;
    Button mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        setAmbientEnabled();
        startService(new Intent(this, DataReceiverWear.class));
        context = this;
        localStorageWear = new LocalStorageWear(context);
        dataSenderWear = new DataSenderWear(context);

        UID = localStorageWear.getSettings().get("UID");
        partnerUID = localStorageWear.getSettings().get("UID"); //change to PARTNERID


        Log.d("7575", UID+partnerUID);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {


            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                stub.setOnTouchListener(new OnSwipeTouchListener(context){
                    @Override
                    public void onSwipeLeft(){
                        Intent intent = new Intent(WearActivity.this, DetailActivity.class);
                        startActivity(intent);

                    }
                    @Override
                    public void onSwipeRight(){
                        Intent intent = new Intent(WearActivity.this, SendMsgActivity.class);
                        intent.putExtra("partnerUID", partnerUID);
                        startActivity(intent);
                    }
                });
                mButton = (Button) findViewById(R.id.button2);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(WearActivity.this, History.class);
                        startActivity(intent);
                    }
                });
            }





        });
    }
}