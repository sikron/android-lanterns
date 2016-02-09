package com.skronawi.laterne;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapter extends PagerAdapter {

    public int getCount() {
        return 5;
    }
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int resId = 0;
        switch (position) {
            case 0:
                resId = R.layout.lantern1;
                break;
            case 1:
                resId = R.layout.lantern2;
                break;
            case 2:
                resId = R.layout.lantern3;
                break;
            case 3:
                resId = R.layout.lantern4;
                break;
            case 4:
                resId = R.layout.lantern5;
                break;
        }
        View view = inflater.inflate(resId, null);
        collection.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        arg0.removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public int determineFolien(int currentItem) {
        int resId = 0;
        switch (currentItem){
            case 0:
                resId = R.id.folien1;
                break;
            case 1:
                resId = R.id.folien2;
                break;
            case 2:
                resId = R.id.folien3;
                break;
            case 3:
                resId = R.id.folien4;
                break;
            case 4:
                resId = R.id.folien5;
                break;
        }
        return resId;
    }

    public int determineLanternBright(int currentItem) {
        int resId = 0;
        switch (currentItem){
            case 0:
                resId = R.drawable.laterne1_hell;
                break;
            case 1:
                resId = R.drawable.laterne2_hell;
                break;
            case 2:
                resId = R.drawable.laterne3_hell;
                break;
            case 3:
                resId = R.drawable.laterne4_hell;
                break;
            case 4:
                resId = R.drawable.laterne5_hell;
                break;
        }
        return resId;
    }

    public int determineLanternDark(int currentItem) {

        int resId = 0;
        switch (currentItem){
            case 0:
                resId = R.drawable.laterne1_dunkel;
                break;
            case 1:
                resId = R.drawable.laterne2_dunkel;
                break;
            case 2:
                resId = R.drawable.laterne3_dunkel;
                break;
            case 3:
                resId = R.drawable.laterne4_dunkel;
                break;
            case 4:
                resId = R.drawable.laterne5_dunkel;
                break;
        }
        return resId;
    }
}