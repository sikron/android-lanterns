package com.skronawi.laterne3d.lanterns;

import android.content.Context;
import com.skronawi.laterne3d.data.VertexArray;
import com.skronawi.laterne3d.geometry.Vector;
import com.skronawi.laterne3d.objects.ObjectBuilder;
import com.skronawi.laterne3d.programs.TextureShaderProgram;
import com.skronawi.laterne3d.util.TextureHelper;

import static android.opengl.Matrix.*;

public abstract class AbstractLantern implements Lantern {

    protected float[] vectorToLightInEyeSpace;
    protected float[] candlePosition; //center of the lantern

    protected float[] modelMatrix = new float[16];
    protected float[] baseModelMatrix = new float[16];

    protected final float[] modelViewProjectionMatrix = new float[16];
    protected final float[] it_modelViewMatrix = new float[16];

    protected final float[] modelViewMatrix = new float[16];
    protected final float[] tempMatrix = new float[16];
    protected ObjectBuilder.GeneratedData body;

    protected ObjectBuilder.GeneratedData bottom;
    protected ObjectBuilder.GeneratedData top;
    protected VertexArray bodyVertexArray;
    protected VertexArray bottomVertexArray;
    protected VertexArray topVertexArray;

    protected float[] skeletalColor;
    protected final float height;

    protected TextureShaderProgram textureProgram;
    protected int bodyTexture;
    protected int topBottomTexture;

    protected Vector rotation;
    protected Vector center;
    protected Vector pivot;
    protected Vector initialRotation;


    public AbstractLantern(float[] skeletalColor, float height) {

        this.skeletalColor = skeletalColor;
        this.height = height;

        rotation = new Vector(0f, 0f, 0f);
        center = new Vector(0f, 0f, 0f);
        pivot = new Vector(0f, 0f, 0f);
        initialRotation = new Vector(0f, 0f, 0f);

        candlePosition = new float[]{0f, 0f, 0f, 1f};
    }

    protected void bindData(VertexArray vertexArray, TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttribPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT,
                textureProgram.getNormalAttributeLocation(),
                NORMAL_COMPONENT_COUNT,
                STRIDE);
    }

    @Override
    public void draw(float[] viewProjectionMatrix, float[] viewMatrix, float lightIntensity) {

        //rotation is a bit tricky here, as the lantern can rotate around its center, the bottom not:
        //http://stackoverflow.com/questions/17630313/rotation-around-a-pivot-point-with-opengl
        //http://stackoverflow.com/questions/13134107/opengl-translate-after-rotate
        //http://gamedev.stackexchange.com/questions/59843/rotating-an-object-when-the-center-in-not-the-origin-opengl
        //http://gamedev.stackexchange.com/questions/16719/what-is-the-correct-order-to-multiply-scale-rotation-and-translation-matrices-f
        //http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/#Cumulating_transformations
        //http://stackoverflow.com/questions/9991803/android-opengl-object-translation-and-rotation-in-the-same-time

        baseModelMatrix(baseModelMatrix);

        //bottom
        modelMatrix = baseModelMatrix.clone();

        translateM(modelMatrix, 0, center.x, center.y - height / 2, center.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

        setIdentityM(modelViewMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(it_modelViewMatrix, 0, tempMatrix, 0);

        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, topBottomTexture,
                modelViewMatrix, it_modelViewMatrix, candlePosition, pointLightColor,
                lightIntensity, skeletalColor, vectorToLightInEyeSpace);
        bindData(bottomVertexArray, textureProgram);
        bottom.drawList.get(0).draw();


        //top
        modelMatrix = baseModelMatrix.clone();

        translateM(modelMatrix, 0, center.x, center.y + height / 2, center.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

        setIdentityM(modelViewMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(it_modelViewMatrix, 0, tempMatrix, 0);

        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, topBottomTexture,
                modelViewMatrix, it_modelViewMatrix, candlePosition, pointLightColor,
                lightIntensity, skeletalColor, vectorToLightInEyeSpace);
        bindData(topVertexArray, textureProgram);
        top.drawList.get(0).draw();



        /*
        1. normal-vektoren berechnen für modelSpace (also vor veränderung der modelmatrix)
        2. nach translate/rotate der modelMatrix: "normal-vektoren mit-rotieren/translaten" -> eyeSpace

            mat3 normalMatrix = transpose(inverse(mat3(modelMatrix)));
            for (normal-vektor of all vertices){
                vec3 transformedNormal = normalize(normalMatrix * normal-vektor);
            }
        3. setze transformedNormals als uniform in den shader
         */

        //1. wurde bereits im builder von cylinderLantern gemacht


        //rotate lantern
        modelMatrix = baseModelMatrix.clone();

        translateM(modelMatrix, 0, center.x, center.y, center.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);


        //2. inverse transponierte berechnen
        setIdentityM(modelViewMatrix, 0);
        multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        invertM(tempMatrix, 0, modelViewMatrix, 0);
        transposeM(it_modelViewMatrix, 0, tempMatrix, 0);

        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, bodyTexture,
                modelViewMatrix, it_modelViewMatrix, candlePosition, pointLightColor,
                lightIntensity, skeletalColor, vectorToLightInEyeSpace);   //3.
        bindData(bodyVertexArray, textureProgram);
        body.drawList.get(0).draw();
    }

    private void baseModelMatrix(float[] baseModelMatrix) {
        setIdentityM(baseModelMatrix, 0);
        translateM(baseModelMatrix, 0, pivot.x, pivot.y, pivot.z);
        rotateM(baseModelMatrix, 0, rotation.x + initialRotation.x, 1f, 0f, 0f);
        rotateM(baseModelMatrix, 0, rotation.y + initialRotation.y, 0f, 1f, 0f);
        rotateM(baseModelMatrix, 0, rotation.z + initialRotation.z, 0f, 0f, 1f);
        translateM(baseModelMatrix, 0, pivot.x, pivot.y, -pivot.z);
    }

    @Override
    public void setRotation(float xRotation, float yRotation, float zRotation) {
        rotation.x = xRotation;
        rotation.y = yRotation;
        rotation.z = zRotation;
    }

    @Override
    public void initTextures(Context context, int body, int topBottom) {
        textureProgram = new TextureShaderProgram(context);
        bodyTexture = TextureHelper.loadTexture(context, body);
        topBottomTexture = TextureHelper.loadTexture(context, topBottom);
    }

    @Override
    public void setLighting(float[] vectorToLightInEyeSpace) {
        this.vectorToLightInEyeSpace = vectorToLightInEyeSpace;
    }

    @Override
    public void setCenter(float x, float y, float z) {
        this.center.x = x;
        this.center.y = y;
        this.center.z = z;
    }

    @Override
    public void setRotationPivotPoint(float x, float y, float z) {
        this.pivot.x = x;
        this.pivot.y = y;
        this.pivot.z = z;
    }

    @Override
    public void setInitialRotation(float x, float y, float z) {
        this.initialRotation.x = x;
        this.initialRotation.y = y;
        this.initialRotation.z = z;
    }
}
