package com.skronawi.laterne3d.util;

import com.skronawi.laterne3d.physics.Vector;

public class PhysicsUtil {

    //in degrees
    public static float[] computeAngles(Vector position) {

        float[] angles = new float[3];

        //x
        angles[0] = compute(position.y, position.z);
        //y
        angles[1] = compute(position.x, position.z);
        //z
        angles[2] = compute(position.y, position.x);

        return angles;
    }

    private static float compute(float a, float b) {
        if (b == 0) {
            return 0;
        } else {
            return (float) Math.toDegrees(Math.atan(a / b)) * -1;
        }
    }
}
