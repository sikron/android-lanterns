package com.skronawi.laterne3d.objects;

import android.util.FloatMath;
import com.skronawi.laterne3d.geometry.*;
import com.skronawi.laterne3d.lanterns.CylinderLantern;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.*;

public class ObjectBuilder {

    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
    private int offset = 0;


    public static interface DrawCommand {
        void draw();

    }

    public static class GeneratedData {

        public final float[] vertexData;

        public final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }

    }

    public static GeneratedData createCylinder(Cylinder cylinder, int numPoints) {

        int size = sizeOfCylinderInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);
        builder.appendCylinder(cylinder, numPoints);
        return builder.build();
    }

    public static GeneratedData createSkewCylinder(Ring upperRing, Ring lowerRing, Point center, float height, int numPoints) {

        int size = sizeOfCylinderInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);
        builder.appendSkewCylinder(upperRing, lowerRing, center, height, numPoints);
        return builder.build();
    }

    public static GeneratedData createCircle(Circle circle, int numPoints) {

        int size = sizeOfCircleInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);
        builder.appendCircle(circle, numPoints);
        return builder.build();
    }

    public static GeneratedData createRing(Ring ring, int numPoints, float thickness) {

        int size = sizeOfRingInVertices(numPoints);
        ObjectBuilder builder = new ObjectBuilder(size);
        builder.appendRing(ring, numPoints, thickness);
        return builder.build();
    }

    private void appendRing(Ring ring, int numPoints, float thickness) {

        final int startVertex = offset / CylinderLantern.POSITION_COMPONENT_COUNT;
        final int numVertices = sizeOfRingInVertices(numPoints);

        float triangleSliceYStart = 1f;
        float triangleSliceYEnd = 0f;

        for (int i = 0; i <= numPoints; i++) {

            float triangleSliceX = i % 2;

            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);
            //outer point
            vertexData[offset++] =
                    ring.center.x
                            + (ring.radius + thickness / 2) * FloatMath.cos(angleInRadians);
            vertexData[offset++] = ring.center.y;
            vertexData[offset++] =
                    ring.center.z
                            + (ring.radius + thickness / 2) * FloatMath.sin(angleInRadians);

            //texture-coord
            vertexData[offset++] = triangleSliceX;
            vertexData[offset++] = triangleSliceYStart;

            //normal zeigt hier immer nach oben, weil der ring immer nur für top benutzt wird
            vertexData[offset++] = 0;
            vertexData[offset++] = -1;
            vertexData[offset++] = 0;

            //inner point
            vertexData[offset++] =
                    ring.center.x
                            + (ring.radius - thickness / 2) * FloatMath.cos(angleInRadians);
            vertexData[offset++] = ring.center.y;
            vertexData[offset++] =
                    ring.center.z
                            + (ring.radius - thickness / 2) * FloatMath.sin(angleInRadians);

            //texture-coord
            vertexData[offset++] = triangleSliceX;
            vertexData[offset++] = triangleSliceYEnd;

            //normal zeigt hier immer nach OBEN, weil der ring immer nur für top benutzt wird
            vertexData[offset++] = 0;
            vertexData[offset++] = -1;
            vertexData[offset++] = 0;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex,
                        numVertices);
            }
        });
    }

    private void appendCircle(Circle circle, int numPoints) {

        final int startVertex = offset / CylinderLantern.POSITION_COMPONENT_COUNT;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        // Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;
        //texture-coord
        vertexData[offset++] = 0.5f;
        vertexData[offset++] = 0.5f;
        //normal zeigt hier immer nach UNTEN, weil der circle immer nur für bottom benutzt wird
        vertexData[offset++] = 0;
        vertexData[offset++] = 1;
        vertexData[offset++] = 0;

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {

            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);

            //point coord
            vertexData[offset++] =
                    circle.center.x
                            + circle.radius * FloatMath.cos(angleInRadians);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] =
                    circle.center.z
                            + circle.radius * FloatMath.sin(angleInRadians);

            //texture-coord
            vertexData[offset++] = 0.5f + 0.5f * FloatMath.cos(angleInRadians);
            vertexData[offset++] = 0.5f + 0.5f * FloatMath.sin(angleInRadians);

            //normal zeigt hier immer nach UNTEN, weil der circle immer nur für bottom benutzt wird
            vertexData[offset++] = 0;
            vertexData[offset++] = 1;
            vertexData[offset++] = 0;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex,
                        numVertices);
            }
        });
    }

    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfRingInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private static int sizeOfCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * (
                CylinderLantern.POSITION_COMPONENT_COUNT +
                        CylinderLantern.TEXTURE_COORDINATES_COMPONENT_COUNT +
                        CylinderLantern.NORMAL_COMPONENT_COUNT)];
    }

    private void appendCylinder(Cylinder cylinder, int numPoints) {

        final int startVertex = offset / CylinderLantern.POSITION_COMPONENT_COUNT;
        final int numVertices = sizeOfCylinderInVertices(numPoints);

        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        Vector normal = null;

        // Generate strip around center point. <= is used because we want to
        // generate the points at the starting angle twice, to complete the
        // strip.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);

            float xPosition =
                    cylinder.center.x
                            + cylinder.radius * FloatMath.cos(angleInRadians);

            float zPosition =
                    cylinder.center.z
                            + cylinder.radius * FloatMath.sin(angleInRadians);

            float triangleSliceX = ((float) i) * 1.0f / ((float) numPoints);
            float triangleSliceYStart = 1f;
            float triangleSliceYEnd = 0f;

            //(same as vertex-"vector" except Y)
            normal = new Vector(xPosition, 0, zPosition).normalize();

            //upper point --------------------------------------------------
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            //texture-coord
            vertexData[offset++] = triangleSliceX;
            vertexData[offset++] = triangleSliceYStart;

            //normal (x und z UMGEKEHRT, da die innenseite beleuchtet werden soll
            //   => die normalen müssen nach innen zeigen)
            vertexData[offset++] = -normal.x;
            vertexData[offset++] = normal.y;
            vertexData[offset++] = -normal.z;

            //lower point --------------------------------------------------
            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;

            //texture-coord
            vertexData[offset++] = triangleSliceX;
            vertexData[offset++] = triangleSliceYEnd;

            //normal
            //the upper vector is reusable
            vertexData[offset++] = -normal.x;
            vertexData[offset++] = normal.y;
            vertexData[offset++] = -normal.z;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex,
                        numVertices);
            }
        });
    }

    private void appendSkewCylinder(Ring upperRing, Ring lowerRing, Point center, float height, int numPoints) {

        final int startVertex = offset / CylinderLantern.POSITION_COMPONENT_COUNT;
        final int numVertices = sizeOfCylinderInVertices(numPoints);

        final float yStart = center.y - (height / 2f);
        final float yEnd = center.y + (height / 2f);

        Vector normal = null;

        // Generate strip around center point. <= is used because we want to
        // generate the points at the starting angle twice, to complete the
        // strip.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);

            float triangleSliceX = ((float) i) * 1.0f / ((float) numPoints);
            float triangleSliceYStart = 1f;
            float triangleSliceYEnd = 0f;


            //upper point --------------------------------------------------
            float xPosition =
                    center.x
                            + lowerRing.radius * FloatMath.cos(angleInRadians);
            float zPosition =
                    center.z
                            + lowerRing.radius * FloatMath.sin(angleInRadians);
            //(same as vertex-"vector" except Y)
            normal = new Vector(xPosition, 0, zPosition).normalize();
            //TODO nicht korrekt, die normalen sind nicht waagrecht, sondern haben einen winkel nach unten/oben

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;
//            Log.d("ChineseLantern", i+". position, oben: "+xPosition+","+yStart);

            //texture-coord
            vertexData[offset++] = triangleSliceX;
            vertexData[offset++] = triangleSliceYStart;

            //normal (x und z UMGEKEHRT, da die innenseite beleuchtet werden soll
            //   => die normalen müssen nach innen zeigen)
            vertexData[offset++] = -normal.x;
            vertexData[offset++] = normal.y;
            vertexData[offset++] = -normal.z;

            //lower point --------------------------------------------------
            xPosition =
                    center.x
                            + upperRing.radius * FloatMath.cos(angleInRadians);
            zPosition =
                    center.z
                            + upperRing.radius * FloatMath.sin(angleInRadians);
            //(same as vertex-"vector" except Y)
            normal = new Vector(xPosition, 0, zPosition).normalize();
            //TODO nicht korrekt, die normalen sind nicht waagrecht, sondern haben einen winkel nach unten/oben

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
//            Log.d("ChineseLantern", i+". position, unten: "+xPosition+","+yEnd);

            //texture-coord
            vertexData[offset++] = triangleSliceX;
            vertexData[offset++] = triangleSliceYEnd;

            //normal
            //the upper vector is reusable
            vertexData[offset++] = -normal.x;
            vertexData[offset++] = normal.y;
            vertexData[offset++] = -normal.z;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }
}
