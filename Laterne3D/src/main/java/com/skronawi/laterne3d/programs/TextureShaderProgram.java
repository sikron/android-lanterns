package com.skronawi.laterne3d.programs;

import android.content.Context;
import com.skronawi.laterne3d.R;

import static android.opengl.GLES20.*;

public class TextureShaderProgram extends ShaderProgram {

    // Uniform locations
    private final int uMVPMatrixLocation;
    private final int uTextureUnitLocation;
    private final int uIT_MVMatrixLocation;
    private final int uPointLightPositionLocation;
    private final int uPointLightColorLocation;
    private final int uMVMatrixLocation;
    private final int uLightIntensity;
    private final int uSkelettColor;
    private final int uVectorToLightLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;
    private final int aNormalLocation;

    public TextureShaderProgram(Context context) {

        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        // Retrieve uniform locations for the shader program.
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);

        uIT_MVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX);
        uPointLightPositionLocation = glGetUniformLocation(program, U_POINT_LIGHT_POSITION);
        uPointLightColorLocation = glGetUniformLocation(program, U_POINT_LIGHT_COLOR);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uLightIntensity = glGetUniformLocation(program, U_LIGHT_INTENSITY);
        uSkelettColor = glGetUniformLocation(program, U_SKELETT_COLOR);
        uVectorToLightLocation = glGetUniformLocation(program, U_VECTOR_TO_LIGHT);
    }

    public void setUniforms(float[] modelViewProjectionMatrix, int textureId,
                            float[] modelViewMatrix, float[] it_modelViewMatrix, float[] pointPositionInEyeSpace, float[] pointLightColor,
                            float lightIntensity, float[] skelettColor, float[] vectorToDirectionalLight) {

        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, modelViewProjectionMatrix, 0);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);


        glUniformMatrix4fv(uMVMatrixLocation, 1, false, modelViewMatrix, 0);
        glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_modelViewMatrix, 0);
        glUniform4fv(uPointLightPositionLocation, 1, pointPositionInEyeSpace, 0);
        glUniform3fv(uPointLightColorLocation, 1, pointLightColor, 0);

        glUniform1f(uLightIntensity, lightIntensity);
        glUniform3fv(uSkelettColor, 1, skelettColor, 0);
        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }

    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }
}