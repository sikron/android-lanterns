package com.skronawi.laterne3d.physics;

import java.util.LinkedList;
import java.util.List;

public class Physics {

    private static final float BOUNDARY = 1f;
    private static final float BUMP_DECELERATION = 0.5f;

    private final long intervalInMillis;
    private List<Particle> particles = new LinkedList<Particle>();
    private List<Force> forces = new LinkedList<Force>();

    public Physics(long intervalInMillis) {
        this.intervalInMillis = intervalInMillis;
    }

    public void manage(Particle particle) {
        particles.add(particle);
    }

    public void addDefaultForce(Force force) {
        forces.add(force);
    }

    public void applyForceVector(Vector force) {

        for (Particle particle : particles) {
            //apply all outer acceleration
            for (Force f : forces) {
                f.apply(particle, intervalInMillis);
            }
            particle.accumulateForce(force);

            update(particle);
            //reset acceleration
            particle.resetForceAccumulator();

            boundary(particle);
        }
    }

    private void boundary(Particle particle) {

        Vector positionInBoundary = particle.getPosition().clone();
        Vector velocityInBoundary = particle.getVelocity().clone();

        /*
        der lantern renderer verändert die übergebenen winkel, somit muss ich hier schauen,
        dass auch die auslenkung wieder ausgleiche.
         */

        if (positionInBoundary.x < -(BOUNDARY * 0.5f)) {
            positionInBoundary.x = -(BOUNDARY * 0.5f);
            velocityInBoundary.x *= -BUMP_DECELERATION; //reverse and damp
        }
        if (positionInBoundary.x > (BOUNDARY * 0.5f)) {
            positionInBoundary.x = (BOUNDARY * 0.5f);
            velocityInBoundary.x *= -BUMP_DECELERATION; //reverse and damp
        }

        if (positionInBoundary.y < -(BOUNDARY * 0.5f)) {
            positionInBoundary.y = -(BOUNDARY * 0.5f);
            velocityInBoundary.y *= -BUMP_DECELERATION; //reverse and damp
        }
        if (positionInBoundary.y > (BOUNDARY * 1.8)) {
            positionInBoundary.y = (BOUNDARY * 1.8f);
            velocityInBoundary.y *= -BUMP_DECELERATION; //reverse and damp
        }

        particle.setPosition(positionInBoundary);
        particle.setVelocity(velocityInBoundary);
    }

    private void update(Particle particle) {

        float intervalInSecs = (float) intervalInMillis / 1000f;  //e.g. 0.033

        //position
        Vector velocity = particle.getVelocity().clone();
        velocity.multiply(intervalInSecs);
        Vector newPosition = particle.getPosition();
        newPosition.add(velocity);
        particle.setPosition(newPosition);

        //velocity
        float dampPowTime = (float) Math.pow(particle.getMass(), intervalInSecs);
        Vector velocityMult = particle.getVelocity().clone();
        velocityMult.multiply(dampPowTime);

        Vector acceleration = particle.getAcceleration().clone();
        acceleration.multiply(intervalInSecs);

        velocityMult.add(acceleration);
        particle.setVelocity(velocityMult);
    }
}
