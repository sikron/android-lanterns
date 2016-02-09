package com.skronawi.laterne3d.util;

public class MatrixHelper {

    /*
    Android’s Matrix class contains two methods for this, frustumM() and perspectiveM(). Unfortunately,
    frustumM() has a bug that affects some types of projections, and perspectiveM()
    was only introduced in Android Ice Cream Sandwich and is not available on
    earlier versions of Android. (http://code.google.com/p/android/issues/detail?id=35646)
     */
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f) {

        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);

        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));

        /*
         column-major order, which means that we write out data one column at a time rather than one row at a time
         */

        //first column
        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        //second column
        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        //third column
        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;

        //fourth column
        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));   //wrong, look into errata (http://pragprog.com/titles/kbogla/errata)
//        m[14] = -((2 * f * n) / (f - n));
        m[15] = 0f;
    }
}
