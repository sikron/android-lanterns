package com.skronawi.laterne3d.lanterns;

import com.skronawi.laterne3d.data.VertexArray;
import com.skronawi.laterne3d.geometry.Circle;
import com.skronawi.laterne3d.geometry.Cylinder;
import com.skronawi.laterne3d.geometry.Point;
import com.skronawi.laterne3d.geometry.Ring;
import com.skronawi.laterne3d.objects.ObjectBuilder;

public class CylinderLantern extends AbstractLantern {

    public CylinderLantern(float radius, float height, int numPointsAround,
                           float[] skeletalColor, float thickness) {

        super(skeletalColor, height);

        body = ObjectBuilder.createCylinder(new Cylinder(
                new Point(0f, 0f, 0f), radius, height), numPointsAround);
        bottom = ObjectBuilder.createCircle(new Circle(
                new Point(0f, 0f, 0f), radius), numPointsAround);
        top = ObjectBuilder.createRing(new Ring(
                new Point(0f, 0f, 0f), radius - thickness / 2), numPointsAround,
                thickness);

        bodyVertexArray = new VertexArray(body.vertexData);
        bottomVertexArray = new VertexArray(bottom.vertexData);
        topVertexArray = new VertexArray(top.vertexData);
    }
}
