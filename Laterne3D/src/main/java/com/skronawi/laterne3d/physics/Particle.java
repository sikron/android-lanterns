package com.skronawi.laterne3d.physics;

public class Particle {

    private static final String TAG = Particle.class.getSimpleName();

    private static final float MASS = 1f / 0.9f;

    private Vector position = new Vector();
    private Vector velocity = new Vector();

    private Vector accelerationAccumulator = new Vector();

    public Particle(Vector position) {
        this.position = position;
    }

    public Particle() {
    }

    public Vector getAcceleration() {
        return accelerationAccumulator;
    }

    public void setAcceleration(Vector acceleration) {
        this.accelerationAccumulator = acceleration;
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
//        Log.d(TAG, "setPosition: " + position);
        this.position = position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
//        Log.d(TAG, "setVelocity: " + velocity);
        this.velocity = velocity;
    }

    public float getMass() {
        return MASS;
    }

    public void accumulateForce(Vector force) {
        accelerationAccumulator.add(force);
//        Log.d(TAG, "accumulateForce: current force " + accelerationAccumulator);
    }

    public void resetForceAccumulator() {
        accelerationAccumulator = new Vector();
    }
}
