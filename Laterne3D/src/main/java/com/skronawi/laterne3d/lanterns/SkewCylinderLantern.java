package com.skronawi.laterne3d.lanterns;

import com.skronawi.laterne3d.data.VertexArray;
import com.skronawi.laterne3d.geometry.Circle;
import com.skronawi.laterne3d.geometry.Point;
import com.skronawi.laterne3d.geometry.Ring;
import com.skronawi.laterne3d.objects.ObjectBuilder;

public class SkewCylinderLantern extends AbstractLantern {

    public SkewCylinderLantern(float upperRadius, float lowerRadius, float height, int numPointsAround,
                               float[] skeletalColor, float thickness) {

        super(skeletalColor, height);

        Ring upper = new Ring(new Point(0f, 0f, 0f), upperRadius);
        Ring lower = new Ring(new Point(0f, 0f, 0f), lowerRadius);

        body = ObjectBuilder.createSkewCylinder(upper, lower, new Point(0f, 0f, 0f),
                height, numPointsAround);
        bottom = ObjectBuilder.createCircle(new Circle(new Point(0f, 0f, 0f), lowerRadius),
                numPointsAround);
        top = ObjectBuilder.createRing(new Ring(new Point(0f, 0f, 0f), upperRadius - thickness / 2),
                numPointsAround, thickness);

        bodyVertexArray = new VertexArray(body.vertexData);
        bottomVertexArray = new VertexArray(bottom.vertexData);
        topVertexArray = new VertexArray(top.vertexData);
    }
}
