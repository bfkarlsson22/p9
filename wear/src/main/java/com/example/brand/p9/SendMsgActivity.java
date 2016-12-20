package com.example.brand.p9;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brand on 12/13/2016.
 */

public class SendMsgActivity extends WearableActivity implements WearableListView.ClickListener {

    private TextView mTextView;
    private Context mContext = this;
    private WearableListView listView;
    String message0 = "I'm going for a walk";
    String message1 = "Good job today";
    String message2 = "I'm almost done";
    String message3 = "Keep up the work";
    private DataSenderWear mMessageSender;
    LocalStorageWear localStorageWear;
    String partnerUID;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmsg);
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                stub.setOnTouchListener(new OnSwipeTouchListener(mContext) {
                    @Override
                    public void onSwipeLeft() {
                        onBackPressed();
                    }

                    @Override
                    public void onSwipeRight() {

                    }
                });
                listView = (WearableListView) stub.findViewById(R.id.sample_list_view);
                loadAdapter();

            }


        });
        mMessageSender = new DataSenderWear(mContext);
        localStorageWear = new LocalStorageWear(mContext);
        partnerUID = localStorageWear.getSettings().get("PARTNER");

    }



    private void loadAdapter() {
        List<SettingsItems> items = new ArrayList<>();
        items.add(new SettingsItems(R.drawable.image, message0));
        items.add(new SettingsItems(R.drawable.image, message1));
        items.add(new SettingsItems(R.drawable.image, message2));
        items.add(new SettingsItems(R.drawable.image, message3));

        SettingsAdapter mAdapter = new SettingsAdapter(this, items);


        listView.setAdapter(mAdapter);

        listView.setClickListener(this);
    }


    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        switch (viewHolder.getPosition()) {
            case 0:
                mMessageSender.sendMsgtoPhone(message0, "false", partnerUID);
                confirmationActivity();
                break;
            case 1:
                mMessageSender.sendMsgtoPhone(message1, "false", partnerUID);
                confirmationActivity();
                Log.d("9999", "click2");
                break;
            case 2:
                mMessageSender.sendMsgtoPhone(message2, "false", partnerUID);
                confirmationActivity();
                Log.d("9999", "click3");
                break;
            case 3:
                mMessageSender.sendMsgtoPhone(message3, "false", partnerUID);
                confirmationActivity();
                Log.d("9999", "click4");
                break;
        }
    }

    @Override
    public void onTopEmptyRegionClick() {
        //Prevent NullPointerException

    }


    public void confirmationActivity(){
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                "Message sent");
        startActivity(intent);
    }
}