package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    ImageButton mIbLike;
    ImageButton mIbDislike;
    Button mLike;
    Button mDislike;
    private DataSenderWear mReplySender;
    String repliedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        mReplySender = new DataSenderWear(mContext);


        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.tv1);
                mTextTime = (TextView) stub.findViewById(R.id.tv2);
             //   mIbLike = (ImageButton) stub.findViewById(R.id.bILike);
              //  mIbDislike = (ImageButton) stub.findViewById(R.id.bIDisLike);
                mDislike = (Button) stub.findViewById(R.id.bDislike2);
                mLike = (Button) stub.findViewById(R.id.bLike2);

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
                            //mIbLike.setVisibility(View.VISIBLE);
                           // mIbDislike.setVisibility(View.VISIBLE);
                            mLike.setVisibility(View.VISIBLE);

                            mLike.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String message = "LIKE";
                                    String reply = "true";
                                    Long time = System.currentTimeMillis();
                                    String currentTime = String.valueOf(time);
                                    mReplySender.sendReply(message, currentTime, reply);
                                    Intent intent = new Intent(Communication.this, WearActivity.class);
                                    startActivity(intent);



                                }
                            });

                            mDislike.setVisibility(View.VISIBLE);
                            mDislike.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String message = "DISLIKE";
                                    String reply = "true";
                                    Long time = System.currentTimeMillis();
                                    String currentTime = String.valueOf(time);
                                    mReplySender.sendReply(message, currentTime, reply);
                                    Intent intent = new Intent(Communication.this, WearActivity.class);
                                    startActivity(intent);
                                }
                            });
                            return false;
                        }
                    });

                }else{mTextView.setText("Your partner has replied to your message: " + messages);}

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
                Log.d("7878", messages+time+reply);
            }
        } else {
            messages = (String) savedInstanceState.getSerializable("message");
        }

    }


  }


