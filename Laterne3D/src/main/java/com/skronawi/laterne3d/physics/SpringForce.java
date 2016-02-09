package com.skronawi.laterne3d.physics;

/*
only pulls; does not push; pulls in every direction
 */
public class SpringForce implements Force {

    private static final String TAG = SpringForce.class.getSimpleName();

    private static final float springConstant = 4f;      //TODO set

    private final Vector origin;
    private float restLength;      //bis wieweit die feder entspannt ist

    public SpringForce(Vector origin, float restLength) {
        this.origin = origin;
        this.restLength = restLength;
    }

    @Override
    public void apply(Particle particle, long duration) {

        /*
        // Calculate the vector of the spring.
        Vector3 force;
        particle->getPosition(&force);
        force -= other->getPosition();

        // Check if the bungee is compressed.
        real magnitude = force.magnitude();
        if (magnitude <= restLength) return;

        //calculate magnitude of the force
        magnitude = springConstant * (restLength - magnitude);

        // Calculate the final force and apply it.
        force.normalize();
        force *= -magnitude;
        particle->addForce(force);
         */

        Vector force = particle.getPosition().clone();
        force.subtract(origin);

        float magnitude = force.length();
        if (magnitude <= restLength) {
            return;
        }

        //Math.abs: zug aus beiden richtungen
        magnitude = springConstant * (Math.abs(restLength - magnitude));

        force.normalize();
        force.multiply(-magnitude);

//        Log.d(TAG, "spring force " + force);
        particle.accumulateForce(force);
    }
}
