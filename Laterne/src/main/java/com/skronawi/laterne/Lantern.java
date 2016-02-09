package com.skronawi.laterne;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.HashMap;

public class Lantern extends Activity {

    private HashMap<Integer, Bitmap> hell = new HashMap<Integer, Bitmap>(10);
    private HashMap<Integer, Drawable> dunkel = new HashMap<Integer, Drawable>(10);

    private BackgroundSound backgroundSound;
    private RefreshTask refreshTask;

    private ViewPager myPager;

    public static final String LANTERN_IDX = "LANTERN_IDX";
    public static final String FIRST_TIME = "FIRST_TIME";
    private MyPagerAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(FIRST_TIME, true)) {
            prefs.edit().putBoolean(FIRST_TIME, false);
            prefs.edit().commit();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        adapter = new MyPagerAdapter();
        myPager = (ViewPager) findViewById(R.id.viewpager_layout);
        myPager.setAdapter(adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        myPager.setCurrentItem(prefs.getInt(LANTERN_IDX, 0));

        myPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Lantern.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(Lantern.LANTERN_IDX, i);
                editor.commit();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }



    public void onResume() {

        super.onResume();

//        if (backgroundSound == null){
            backgroundSound = new BackgroundSound(this);
            backgroundSound.execute();
//        } else {
//            backgroundSound.onResume();
//        }

        refreshTask = new RefreshTask(this);
        refreshTask.execute();
    }

    public void onPause() {

        super.onPause();

        backgroundSound.onCancelled();
        backgroundSound = null;
//        backgroundSound.onPause();

        refreshTask.onCancelled();
        refreshTask = null;
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (backgroundSound != null){
            backgroundSound.onCancelled();
            backgroundSound = null;
        }

        if (refreshTask != null){
            refreshTask.onCancelled();
            refreshTask = null;
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (backgroundSound != null){
            backgroundSound.onCancelled();
            backgroundSound = null;
        }

        if (refreshTask != null){
            refreshTask.onCancelled();
            refreshTask = null;
        }
    }

    public void switchGlow(boolean on) {

        Drawable folien;

        if (!on) {
            folien = dunkel.get(adapter.determineLanternDark(myPager.getCurrentItem()));
            if (folien == null){
                folien = getResources().getDrawable(
                        adapter.determineLanternDark(myPager.getCurrentItem()));
                dunkel.put(adapter.determineLanternDark(myPager.getCurrentItem()), folien);
            }
            ((ImageView) findViewById(
                    adapter.determineFolien(myPager.getCurrentItem()))).setImageDrawable(folien);

        } else {

            int currentItem = myPager.getCurrentItem();
            if (hell.get(currentItem) == null){

                // An added margin to the initial image
//                int margin = 24;
//                int halfMargin = margin / 2;

                // the glow radius
                int glowRadius = 10;

                // the glow color
                int glowColor = Color.rgb(0, 192, 255);

                // The original image to use
                Bitmap tmpSrc = BitmapFactory.decodeResource(getResources(),
                        adapter.determineLanternBright(myPager.getCurrentItem()));
                // extract the alpha from the source image
                Bitmap tmpAlpha = tmpSrc.extractAlpha();

                // The output bitmap (with the icon + glow)
                Bitmap tmpBmp = Bitmap.createBitmap(tmpSrc.getWidth()
//                        + margin
                        , tmpSrc.getHeight()
//                                + margin
                        , Bitmap.Config.ARGB_8888);

                // The canvas to paint on the image
                Canvas canvas = new Canvas(tmpBmp);

                Paint paint = new Paint();
                paint.setColor(glowColor);

                // outer glow
                paint.setMaskFilter(new BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.OUTER));
//                canvas.drawBitmap(alpha, halfMargin, halfMargin, paint);
                canvas.drawBitmap(tmpAlpha, 0, 0, paint);

                // original icon
//                canvas.drawBitmap(src, halfMargin, halfMargin, null);
                canvas.drawBitmap(tmpSrc, 0, 0, null);

                hell.put(currentItem, tmpBmp);
            }

            ImageView image = (ImageView) findViewById(
                    adapter.determineFolien(myPager.getCurrentItem()));
            image.setImageBitmap(hell.get(currentItem));
        }
    }
}
