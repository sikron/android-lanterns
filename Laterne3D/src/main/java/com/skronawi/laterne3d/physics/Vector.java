package com.skronawi.laterne3d.physics;

public class Vector {

    public float x = 0f;
    public float y = 0f;
    public float z = 0f;

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector() {
    }

    public Vector clone() {
        return new Vector(x, y, z);
    }

    public void add(Vector other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
    }

    public void multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
    }

    public void normalize() {
        float length = length();
        if (length != 0) {
            this.x /= length;
            this.y /= length;
            this.z /= length;
        }
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x
                + this.y * this.y
                + this.z * this.z);
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public void subtract(Vector other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
    }
}
