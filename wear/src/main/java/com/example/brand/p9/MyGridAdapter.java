package com.example.brand.p9;
/*
import android.content.Context;
import android.graphics.Color;
import android.support.wearable.view.GridPagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by brand on 12/12/2016.
 */

/*

 mPager = (GridViewPager) stub.findViewById(R.id.pager);
                dotsPageIndicator  = (DotsPageIndicator) stub.findViewById(R.id.page_indicator);
                res = getResources();
                mPager.setAdapter(new MyGridAdapter(mContext, mImages));
                dotsPageIndicator.setPager(mPager);
 */
/*public class MyGridAdapter extends GridPagerAdapter {
    Context mContext;
    int[] mImages;

    public MyGridAdapter(Context context, int[] mImages) {
        mContext = context;
        this.mImages = mImages;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return 3;
    }

    //---Go to current column when scrolling up or down (instead of default column 0)---
    @Override
    public int getCurrentColumnForRow(int row, int currentColumn) {
        return currentColumn;
    }

    //---Return our car image based on the provided row and column---
    @Override
    public Object instantiateItem(ViewGroup viewGroup, int row, int col) {
        // Trick to use single dimensional array as multidimensional array.
        int imageToDisplay = 0;
        switch(row){
            case 0:
                imageToDisplay = row + col;     // results are 0, 1, 2
                break;
            case 1:
                imageToDisplay = row + col + 2;     // results are 3, 4, 5
                break;

        }
        ImageView imageView;
        imageView = new ImageView(mContext);
        imageView.setImageResource(mImages[imageToDisplay]);
        imageView.setBackgroundColor(Color.parseColor("#26ce61"));
        viewGroup.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int i, int i2, Object o) {
        viewGroup.removeView((View) o);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {


        return view.equals(o);
    }

}*/