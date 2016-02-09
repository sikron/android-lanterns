package com.skronawi.laterne3d.lanterns;

import android.content.Context;
import com.skronawi.laterne3d.Constants;

public interface Lantern {

    public static final int POSITION_COMPONENT_COUNT = 3;
    public static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    public static final int NORMAL_COMPONENT_COUNT = 3;
    public static final int STRIDE =
            (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT)
                    * Constants.BYTES_PER_FLOAT;

    public final float[] pointLightColor = new float[]{1.0f, 1.0f, 1.0f};  // rgb without alpha


    void draw(float[] viewProjectionMatrix, float[] viewMatrix, float lightIntensity);

    void setRotation(float xRotation, float yRotation, float zRotation);

    void initTextures(Context context, int body, int topBottom);

    void setLighting(float[] vectorToLightInEyeSpace);

    void setCenter(float x, float y, float z);

    void setRotationPivotPoint(float x, float y, float z);

    void setInitialRotation(float x, float y, float z);
}
