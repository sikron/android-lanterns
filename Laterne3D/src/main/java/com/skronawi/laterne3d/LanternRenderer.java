package com.skronawi.laterne3d;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.preference.PreferenceManager;
import com.skronawi.laterne3d.lanterns.Lantern;
import com.skronawi.laterne3d.lanterns.LanternFactory;
import com.skronawi.laterne3d.objects.Skybox;
import com.skronawi.laterne3d.programs.SkyboxShaderProgram;
import com.skronawi.laterne3d.util.MatrixHelper;
import com.skronawi.laterne3d.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

public class LanternRenderer implements GLSurfaceView.Renderer {

    /*
    orthographic projection matrix for orientation; converts my virtual coordinates into
    normalized device coordinates
    */
    private final float[] projectionMatrix = new float[16];

    /*
    vertex_clip  = ProjectionMatrix * ViewMatrix * ModelMatrix * vertex_model
     */
    private final float[] viewMatrix = new float[16];
    private final float[] viewMatrixSkybox = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] viewProjectionMatrixSkybox = new float[16];

    private final Context context;

    private float xRotation;
    private float zRotation;

    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;

    private final float[] vectorToLight = {-0.4f, 0.5f, 1.0f, 0f};  //must be normalized!

    //lanterns
    private Lantern lantern;
    private int currentLanternIdx = 0;
    private float candleLightIntensity = 0;


    public LanternRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);  //black

        glEnable(GL_DEPTH_TEST);

        skyboxProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox();
        skyboxTexture = TextureHelper.loadCubeMap(context,
                new int[]{R.drawable.skybox_bottom, R.drawable.skybox_bottom,
                        R.drawable.skybox_bottom, R.drawable.skybox_bottom,
                        R.drawable.skybox_bottom, R.drawable.skybox_stars});

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        currentLanternIdx = prefs.getInt(Constants.LANTERN_IDX, 0);
        setLantern();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height,
                1f, 10f); //-1 to -10

        setLookAtM(viewMatrix, 0,
                0f, -0.3f, 0.5f,
                0f, -0.47f, 0f,
                0f, 1f, 0f);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height,
                1f, 10f); //-1 to -10

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        setIdentityM(viewMatrixSkybox, 0);
        multiplyMM(viewProjectionMatrixSkybox, 0, projectionMatrix, 0, viewMatrixSkybox, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glDepthFunc(GL_LEQUAL); //This avoids problems with the skybox itself getting clipped.
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(viewProjectionMatrixSkybox, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
        glDepthFunc(GL_LESS); //reset

        //the outer lighting by e.g. the moon
        final float[] vectorToLightInEyeSpace = new float[4];
        multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0);

        lantern.setRotation(xRotation, 0, zRotation);
        lantern.setLighting(vectorToLightInEyeSpace);
        lantern.draw(viewProjectionMatrix, viewMatrix, candleLightIntensity);
    }

    public void changeOrientation(float pitch, float roll, float azimuth) {
        xRotation = pitch;
        zRotation = azimuth;
    }

    public void changeCandleLight(float intensity) {
        this.candleLightIntensity = intensity;
    }

    public void nextLantern() {
        currentLanternIdx++;
        if (currentLanternIdx > 3) {
            currentLanternIdx = 0;
        }
        setLantern();
    }

    public void previousLantern() {
        currentLanternIdx--;
        if (currentLanternIdx < 0) {
            currentLanternIdx = 3;
        }
        setLantern();
    }

    private void setLantern() {

        switch (currentLanternIdx) {
            case 0:
                lantern = LanternFactory.createOrangeCylinderLantern(context);
                break;
            case 1:
                lantern = LanternFactory.createFlowerLantern(context);
                break;
            case 2:
                lantern = LanternFactory.createAndroidLantern(context);
                break;
            case 3:
                lantern = LanternFactory.createDotsLantern(context);
                break;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(Constants.LANTERN_IDX, currentLanternIdx);
        editor.commit();
    }
}
