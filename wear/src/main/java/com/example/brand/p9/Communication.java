package com.example.brand.p9;

import android.content.Context;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by brand on 12/8/2016.
 */

public class Communication extends WearableActivity {

    TextView mTextView;
    TextView mTextTime;
    String messages;
    String time;
    Context mContext = this;
    String reply;
    ImageButton mbLike;
    ImageButton mbDislike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);


        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {


                mTextView = (TextView) stub.findViewById(R.id.tv1);
                mTextTime = (TextView) stub.findViewById(R.id.tv2);
                mbLike = (ImageButton) stub.findViewById(R.id.bLike);
                mbDislike = (ImageButton) stub.findViewById(R.id.bDisLike);



                if (messages != null) {
                    mTextView.setText(messages);
                }
                if (time != null) {
                    mTextTime.setText("Sent at: " + time);
                }

                if(reply.equals("false")){

                 //   mTextView.setClickable(true);
                    mTextView.setLongClickable(true);
                    mTextView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {

                            mTextView.setVisibility(View.GONE);
                            mTextTime.setVisibility(View.GONE);
                            mbLike.setVisibility(View.VISIBLE);
                            mbDislike.setVisibility(View.VISIBLE);

                            //---Assigns an adapter to provide the content for this pager---




                            return false;
                        }
                    });

                }


            }


        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                messages = null;
            } else {
                messages = extras.getString("message");
                time = extras.getString("time");
                reply = extras.getString("reply");
                Log.d("0000", messages+time+reply);





            }
        } else {
            messages = (String) savedInstanceState.getSerializable("message");
        }

    }


  }


