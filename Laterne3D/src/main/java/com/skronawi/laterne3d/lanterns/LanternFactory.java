package com.skronawi.laterne3d.lanterns;

import android.content.Context;
import com.skronawi.laterne3d.R;

public class LanternFactory {

    public static Lantern createOrangeCylinderLantern(Context context) {

        CylinderLantern lantern = new CylinderLantern(1f, 2.5f, 8,
                new float[]{33f / 255f, 33f / 255f, 35f / 255f}, 0.2f);
        lantern.setCenter(0f, -2f, -5f);
        lantern.setInitialRotation(-40f, 10f, 0f);
        lantern.setRotationPivotPoint(0f, 0f, -5f);
        lantern.initTextures(context, R.drawable.lantern_skewed_stripes,
                R.drawable.lantern_skewed_stripes_skelet);
        return lantern;
    }

    public static Lantern createAndroidLantern(Context context) {

        CylinderLantern lantern = new CylinderLantern(1f, 2.5f, 256,
                new float[]{10f / 255f, 34f / 255f, 58f / 255f}, 0.2f);
        lantern.setCenter(0f, -2f, -5f);
        lantern.setInitialRotation(-40f, 0f, 0f);
        lantern.setRotationPivotPoint(0f, 0f, -5f);
        lantern.initTextures(context, R.drawable.lantern_android,
                R.drawable.lantern_android_skelet);
        return lantern;
    }

    public static Lantern createFlowerLantern(Context context) {

        SkewCylinderLantern lantern = new SkewCylinderLantern(1.1f, 0.9f, 2.4f, 4,
                new float[]{9f / 255f, 32f / 255f, 11f / 255f}, 0.2f);
        lantern.setCenter(0f, -2f, -5f);
        lantern.setInitialRotation(-40f, 10f, 0f);
        lantern.setRotationPivotPoint(0f, 0f, -5f);
        lantern.initTextures(context, R.drawable.lantern_flowers,
                R.drawable.lantern_flowers_skelet);
        return lantern;
    }

    public static Lantern createDotsLantern(Context context) {

        CylinderLantern lantern = new CylinderLantern(0.9f, 2.2f, 256,
                new float[]{37f / 255f, 14f / 255f, 8f / 255f}, 0.2f);
        lantern.setCenter(0f, -2f, -5f);
        lantern.setInitialRotation(-40f, 0f, 0f);
        lantern.setRotationPivotPoint(0f, 0f, -5f);
        lantern.initTextures(context, R.drawable.lantern_dots,
                R.drawable.lantern_dots_skelet);
        return lantern;
    }
}
