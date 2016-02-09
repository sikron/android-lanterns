package com.skronawi.laterne3d.physics;

public class DragForce implements Force {

    private static final String TAG = DragForce.class.getSimpleName();

    private final float k1;
    private final float k2;

    public DragForce(float dragLowVelocity, float dragHighVelocity) {
        this.k1 = dragLowVelocity;
        this.k2 = dragHighVelocity;
    }

    @Override
    public void apply(Particle particle, long duration) {

        Vector drag = particle.getVelocity().clone();
        float magnitude = drag.length();

        float dragCoeff =
                k1 * magnitude
                        + k2 * magnitude * magnitude;

        drag.normalize();

        drag.multiply(-dragCoeff);

//        Log.d(TAG, "drag force " + drag);
        particle.accumulateForce(drag);
    }
}
