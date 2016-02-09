package com.skronawi.laterne3d;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.skronawi.laterne3d.music.BackgroundMusic;
import com.skronawi.laterne3d.physics.*;
import com.skronawi.laterne3d.util.PhysicsUtil;

import java.util.Timer;
import java.util.TimerTask;

public class Lantern3DActivity extends Activity implements SensorEventListener {

    private static final String TAG = Lantern3DActivity.class.getSimpleName();

    private static final float[] VECTOR_DOWN = new float[]{0f, 0f, -1f, 1f};   //wegen coord remap
    private final int axisSwap[][] = {
            {1, -1, 0, 1},     // ROTATION_0
            {-1, -1, 1, 0},     // ROTATION_90
            {-1, 1, 0, 1},     // ROTATION_180
            {1, 1, 1, 0}}; // ROTATION_270

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet;
    private Button optionsButton;

    private LanternRenderer lanternRenderer;

    private BackgroundMusic backgroundMusic;

    private SensorManager sensorManager;

    private Timer timer;
    private float[] currentVectorDown = VECTOR_DOWN.clone();
    private Particle downParticle = new Particle(); //the 2D projection of the vector downwards
    private Particle lanternParticle = new Particle();
    private float[] currentAcceleration = new float[3];
    private Physics physics;
    private CandleLight candleLight;
    private Sensor rotationSensor;

    private Sensor accelerationSensor;
    private boolean doPendulum;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //stay awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(Constants.FIRST_TIME, true)) {
            prefs.edit().putBoolean(Constants.FIRST_TIME, false);
            prefs.edit().putBoolean("pref_music", true);
            prefs.edit().putBoolean("pref_pendulum", true);
            prefs.edit().putBoolean("pref_lantern_change", true);
            prefs.edit().commit();
        }

        //show preferences button and glSurfaceView
        setContentView(R.layout.main);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);
        optionsButton = (Button) findViewById(R.id.pref_button);
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Lantern3DActivity.this, Preferences.class));
            }
        });

        //check openGl ES 2
        ActivityManager systemService = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = systemService.getDeviceConfigurationInfo();
        if (supportsEs2(configurationInfo)) {

            glSurfaceView.setEGLContextClientVersion(2);
            lanternRenderer = new LanternRenderer(this);
            glSurfaceView.setRenderer(lanternRenderer);
            rendererSet = true;

        } else {
            Toast.makeText(this, getResources().getString(R.string.error_no_opengl),
                    Toast.LENGTH_LONG).show();
            return;
        }

        //init sensors
        findSensors();
        if (!hasSensors()) {
            Toast.makeText(this, getResources().getString(R.string.error_no_sensors),
                    Toast.LENGTH_LONG).show();
            doPendulum = false;
        } else {
            doPendulum = true;
        }

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            float previousX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Lantern3DActivity.this);
                if (!prefs.getBoolean("pref_lantern_change", true)) {
                    return true;
                }

                if (event != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX();

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        final float deltaX = event.getX() - previousX;

                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                if (deltaX < 0) {
                                    lanternRenderer.nextLantern();
                                } else if (deltaX > 0) {
                                    lanternRenderer.previousLantern();
                                }
                            }
                        });
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });

        initPhysics();
    }

    private void findSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private boolean hasSensors() {
        return rotationSensor != null && accelerationSensor != null;
    }

    private boolean supportsEs2(ConfigurationInfo configurationInfo) {
        return configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
        )
        );
    }

    private void startHearbeat(long interval) {

        final Handler handler = new Handler();
        timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onHeartbeat();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, interval);
    }

    private void stopHeartbeat() {

        timer.cancel();
    }


    private void initPhysics() {

        physics = new Physics(Constants.INTERVAL);
        //set the position as reference for dynamic usage in spring force
        physics.addDefaultForce(new SpringForce(downParticle.getPosition(), 0.0f));
        physics.addDefaultForce(new DragForce(0.4f, 0.7f));
        physics.manage(lanternParticle);

        candleLight = new CandleLight();
    }

    public void startMusic() {
        stopMusic();
        backgroundMusic = new BackgroundMusic(this);
        backgroundMusic.execute();
    }

    public void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.onCancelled();
            backgroundMusic = null;
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        stopMusic();

        if (rendererSet) {
            glSurfaceView.onPause();
        }

        sensorManager.unregisterListener(this);

        stopHeartbeat();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (rendererSet) {
            glSurfaceView.onResume();
        }

        if (hasSensors()) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }

        //set preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        doPendulum = prefs.getBoolean("pref_pendulum", true);
        if (prefs.getBoolean("pref_music", true)) {
            startMusic();
        }

        startHearbeat(Constants.INTERVAL);
    }

    @Override
    protected void onStop() {

        super.onStop();

        stopMusic();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        stopMusic();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (!hasSensors()) {
            return;
        }

        float[] rotationMatrix;
        float[] remappedRotationMatrix;

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ROTATION_VECTOR:

                rotationMatrix = new float[16];
                remappedRotationMatrix = new float[16];

                try {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                } catch (IllegalArgumentException iae) {
                    if (iae.getMessage().contains("array length must be 3 or 4")){
                        //https://github.com/barbeau/gpstest/issues/39
                        float[] truncatedValues = new float[4];
                        System.arraycopy(event.values, 0, truncatedValues, 0, 4);
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, truncatedValues);
                    } else {
                        throw iae;
                    }
                }

                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z,
                        remappedRotationMatrix); //sonst gimbal lock, wenn aufrecht :-(
                determineOrientation(remappedRotationMatrix);
                break;

            case Sensor.TYPE_ACCELEROMETER:

                currentAcceleration = adjustAccelOrientation(
                        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation(),
                        event.values.clone());
                break;
        }
    }

    /*
    on big tablets like nexus 10, the screen orientation must be compensated for:
    - http://stackoverflow.com/questions/5877780/orientation-from-android-accelerometer
    - http://android-developers.blogspot.de/2010/09/one-screen-turn-deserves-another.html
     */
    private float[] adjustAccelOrientation(int displayRotation, float[] eventValues) {

        float[] adjustedValues = new float[3];

        final int[] as = axisSwap[displayRotation];
        adjustedValues[0] = (float) as[0] * eventValues[as[2]];
        adjustedValues[1] = (float) as[1] * eventValues[as[3]];
        adjustedValues[2] = eventValues[2];

        return adjustedValues;
    }

    private void determineOrientation(float[] rotationMatrix) {
        //compute current vector downwards
        float[] tmp = new float[4];
        Matrix.multiplyMV(tmp, 0, rotationMatrix, 0, VECTOR_DOWN, 0);
        currentVectorDown = tmp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not implemented
    }

    private void onHeartbeat() {

        if (hasSensors() && doPendulum) {

            float[] vectorDownClone = currentVectorDown.clone();
//            Log.d(TAG, "current vector down: " + Arrays.toString(vectorDownClone));

            //the springforce has a reference to downParticle.getPosition() !
            downParticle.getPosition().x = vectorDownClone[0];
            downParticle.getPosition().y = vectorDownClone[1];

            float[] accelerationClone = currentAcceleration.clone();
//            Log.d(TAG, "current acceleration: " + Arrays.toString(accelerationClone));

            //compute force between downparticle and lanternparticle and update position of lanternparticle
            physics.applyForceVector(new Vector(-accelerationClone[0], accelerationClone[2], 0));

            //project into 3D space so that rotation angles can be computed;
            Vector position = lanternParticle.getPosition();
            Vector projection = new Vector(position.x, position.y, -1f);
//            Log.d(TAG, "new lantern position: " + position);
//            Log.d(TAG, "lantern projection: " + projection);

            float[] rotation = PhysicsUtil.computeAngles(projection);
//            Log.d(TAG, "computed rotation: " + Arrays.toString(rotation));

            lanternRenderer.changeOrientation(rotation[0], 0, rotation[1]);

        } else {
            lanternRenderer.changeOrientation(40, 0, 0);
        }

        lanternRenderer.changeCandleLight(candleLight.flicker());
    }
}
