package com.example.brand.p9;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by brand on 12/8/2016.
 */

public class Messages extends WearableActivity {

    TextView mTextView;
    String messages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

         WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
            mTextView = (TextView) stub.findViewById(R.id.tv1);
                if(messages != null) {
                    mTextView.setText(messages);
                }
            }


        });
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                messages= null;
            } else {
                messages= extras.getString("message");
                Log.d("1111", messages);
           }
        } else {
            messages= (String) savedInstanceState.getSerializable("message");
        }

}}
