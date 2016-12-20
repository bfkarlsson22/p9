package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by brand on 12/8/2016.
 */

public class ReadReplyActivity extends WearableActivity {

    TextView mTextView;
    TextView mTextTime;
    String messages;
    String time;
    Context mContext = this;
    String reply;
    Button mLike;
    Button mDislike;
    private DataSenderWear mReplySender;
    String userUID;
    String partnerUID;
    Context context;
    String partnerName;




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
                mDislike = (Button) stub.findViewById(R.id.bDislike2);
                mLike = (Button) stub.findViewById(R.id.bLike2);

                if (messages != null) {
                    mTextView.setText(messages);
                }
                if (time != null) {


                    Long timer = Long.valueOf(time);
                    java.text.SimpleDateFormat simpleDateFormat1 = new java.text.SimpleDateFormat("E-k:m");
                    String timeDate = simpleDateFormat1.format(new Date(timer));
                    String formatDay = formatDate(timeDate);
                    mTextTime.setText("Sent at: " + formatDay);
                }
                if(reply.equals("false")){
                  falseReply();

                }else{
                  trueReply();

                }
            }


        });
        context = this;
        //as
        LocalStorageWear localStorageWear = new LocalStorageWear(context);
        userUID = localStorageWear.getSettings().get("UID");
        partnerUID = localStorageWear.getSettings().get("PARTNER");
        partnerName = localStorageWear.getSettings().get("PARTNERNAME");

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
        }

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500, 50, 300};
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
    }


    public void falseReply(){
        mTextView.setLongClickable(true);
        mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mTextView.setVisibility(View.GONE);
                mTextTime.setVisibility(View.GONE);
                mLike.setVisibility(View.VISIBLE);
                mLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message = "LIKE";
                        String reply = "true";
                        mReplySender.sendMsgtoPhone(message, reply, partnerUID);
                        Intent intent = new Intent(ReadReplyActivity.this, WearActivity.class);
                        startActivity(intent);
                    }
                });
                mDislike.setVisibility(View.VISIBLE);
                mDislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message = "DISLIKE";
                        String reply = "true";
                        mReplySender.sendMsgtoPhone(message, reply,partnerUID);
                        Intent intent = new Intent(ReadReplyActivity.this, WearActivity.class);
                        startActivity(intent);
                    }
                });
                return false;
            }
        });
    }

    public void trueReply(){
        mTextView.setText(partnerName +" has replied: " + messages);
        mTextView.setLongClickable(true);
        mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(ReadReplyActivity.this, WearActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }

    private String formatDate(String day){
        HashMap<String, String> days = new HashMap<>();
        days.put("Mon","Mandag");
        days.put("Tue","Tirsdag");
        days.put("Wed","Onsdag");
        days.put("Thu","Torsdag");
        days.put("Fri","Fredag");
        days.put("Sat","Lørdag");
        days.put("Sun","Søndag");

        String[] splitDay = day.split("-");
        String translatedDay = days.get(splitDay[0]);

        String formattedDay = day.replace(splitDay[0],translatedDay);
        return formattedDay;
    }
  }


