package com.skronawi.laterne3d.physics;

import java.util.Random;

public class CandleLight {

    //of the candle, not of the vectorLight
    private boolean goBrighter;
    private int lightIntensityCurrent; //0..100
    private int lightIntensityMin = 40;  //min overall
    private int lightIntensityMax = 100; //max overall
    private int lightIntensityCurrMin = lightIntensityMin; //min until next switch of goBrighter
    private int lightIntensityCurrMax = lightIntensityMax; //max until next switch of goBrighter
    private final int LIGHT_INTENSITY_STEP = 2;    //min and max must be dividable without remainder

    public CandleLight() {

        lightIntensityCurrent = lightIntensityCurrMin;
        goBrighter = true;
    }

    public float flicker() {

        /*
        - between 0 and 1
        - in 2 sec from 0 to 1 => 0.01 per 20 ms (ca.)
         */

        //light
        if (goBrighter) {
            lightIntensityCurrent += LIGHT_INTENSITY_STEP;
        } else {
            lightIntensityCurrent -= LIGHT_INTENSITY_STEP;
        }

        if (lightIntensityCurrent == lightIntensityCurrMax
                || lightIntensityCurrent == lightIntensityCurrMin) {

            if (lightIntensityCurrent == lightIntensityCurrMax) {
                findLightIntensityMin();
            } else {
                findLightIntensityMax();
            }

            goBrighter = !goBrighter;
        }

        return lightIntensityCurrent / 100f;
    }

    /*
    - sets lightIntensityCurrMin.
    - dividable by 2 without remainder.
    - currMin <= currMax
    - new value is in [overAllMin, currMax]
     */
    private void findLightIntensityMin() {

        int diff = lightIntensityCurrMax - lightIntensityMin;
        int toAdd = new Random().nextInt(diff);
        if (toAdd % 2 == 1) {
            toAdd--;
        }
        lightIntensityCurrMin = lightIntensityMin + toAdd;
    }

    //same for max
    private void findLightIntensityMax() {

        int diff = lightIntensityMax - lightIntensityCurrMin;
        int toSubtract = new Random().nextInt(diff);
        if (toSubtract % 2 == 1) {
            toSubtract--;
        }
        lightIntensityCurrMax = lightIntensityMax - toSubtract;
    }
}
