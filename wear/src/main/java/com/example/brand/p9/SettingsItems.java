package com.example.brand.p9;

/**
 * Created by brand on 12/13/2016.
 */

public class SettingsItems {

    public SettingsItems(int iconRes, String title) {
        this.iconRes = iconRes;
        this.title = title;
    }

    public SettingsItems(String day, String title){
        this.day = day;
        this.title = title;
    }
    public int iconRes;
    public String title;
    public String day;

}