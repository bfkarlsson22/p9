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
    private static final long CONNECTION_TIME_OUT_MS = 5000;

    private TextView mTextView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    public SensorEventListener mStepListener;
    private  String MESSAGE;
    private Button mSend;
    private int mNoSteps;
    private Float mFloatSteps;
    private GoogleApiClient client;
    private String nodeId;
    private String TAG = "ABCD";

    private static final String COUNT_KEY = "com.example.key.count";

    private GoogleApiClient mGoogleApiClient;
    private int count = 0;
    private boolean nodeConnected = false;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        mSend = (Button) findViewById(R.id.btn_msg);
        setAmbientEnabled();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                setupWidgets();
            }


        });
        mGoogleApiClient = new ApiClientBuilder(this).buildClient();

        setUpStepCounter();
          /*  FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("1234");

            myRef.setValue("It works!");*/


    }

    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d("ODC","CALLED");
        Log.d("ODC EVENTS", String.valueOf(dataEventBuffer.getCount()));
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/count") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    public void setupWidgets() {
        /*findViewById(R.id.btn_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String connected = String.valueOf(mGoogleApiClient.isConnected());
                Log.d("Connected send",connected);
                Long time = System.currentTimeMillis();
                PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count/"+time);
                putDataMapReq.getDataMap().putInt(COUNT_KEY, count++);
                PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
                PendingResult<DataApi.DataItemResult> pendingResult =
                        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
                Log.d("9999", String.valueOf(count));
            }
        });*/
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                Intent intent = new Intent(context,TestActivity.class);
                startActivity(intent);*/

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
                        mFloatSteps = (event.values[0]);
                        mNoSteps = Math.round(mFloatSteps);
                        final String time = String.valueOf(System.currentTimeMillis());

                        SQLite sqLite = new SQLite(context);
                        sqLite.addData(System.currentTimeMillis(),"STEP",1);

                        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count/"+time);
                        putDataMapReq.getDataMap().putInt("STEP",1);
                        final PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
                        PendingResult<DataApi.DataItemResult> result= Wearable.DataApi.putDataItem(mGoogleApiClient,putDataReq);
                        result.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                            @Override
                            public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                                DataApi.DataItemResult result = dataItemResult;

                                boolean success = result.getStatus().isSuccess();
                                String status = result.getStatus().getStatusMessage();

                                if(success){
                                    Log.d("DATA SENT","SENT / "+status);
                                    Log.d("TIME",time);
                                } else {
                                    Log.d("DATA NOT SENT", status);
                                }
                            }
                        });
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