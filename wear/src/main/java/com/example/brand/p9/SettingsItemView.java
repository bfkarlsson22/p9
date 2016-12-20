package com.example.brand.p9;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by brand on 12/13/2016.
 */

public final class SettingsItemView extends FrameLayout implements WearableListView.OnCenterProximityListener {

    final ImageView image;
    final TextView text;
    final TextView text1;

    public SettingsItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.wearablelistview_item, this);
        image = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        text1 = (TextView) findViewById(R.id.text1);

    }


    @Override
    public void onCenterPosition(boolean b) {

        //Animation example to be ran when the view becomes the centered one
        image.animate().scaleX(1f).scaleY(1f).alpha(1);
        text1.animate().scaleX(1f).scaleY(1f).alpha(1);
        text.animate().scaleX(1f).scaleY(1f).alpha(1);


    }

    @Override
    public void onNonCenterPosition(boolean b) {

        //Animation example to be ran when the view is not the centered one anymore
        image.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
        text.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
        text1.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
    }
}