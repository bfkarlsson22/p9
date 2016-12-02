package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class WearActivity extends WearableActivity {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    public SensorEventListener mStepListener;

    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        setAmbientEnabled();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                setupWidgets();
            }


        });
        setUpStepCounter();
    }
    public void setupWidgets() {
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLite sqLite = new SQLite(context);
                Cursor data = sqLite.getData();

                String cursorData = DatabaseUtils.dumpCursorToString(data);
                Log.d("CURSOR DATA",cursorData);
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

                        SQLite sqLite = new SQLite(context);
                        sqLite.addData(System.currentTimeMillis(),"STEP",1);

                        DataManager dataManager = new DataManager(context);
                        dataManager.sendData();
                        Log.d("REACHED","TRUE");
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