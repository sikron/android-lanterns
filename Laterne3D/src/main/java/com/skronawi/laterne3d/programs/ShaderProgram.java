package com.skronawi.laterne3d.programs;

import android.content.Context;
import com.skronawi.laterne3d.util.ShaderHelper;
import com.skronawi.laterne3d.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

abstract class ShaderProgram {

    // Uniform constants
    protected static final String U_MVP_MATRIX = "u_MVPMatrix";
    protected static final String U_COLOR = "u_Color";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_IT_MV_MATRIX = "u_IT_MVMatrix";
    protected static final String U_POINT_LIGHT_POSITION = "u_PointLightPosition";
    protected static final String U_POINT_LIGHT_COLOR = "u_PointLightColor";
    protected static final String U_MV_MATRIX = "u_MVMatrix";
    protected static final String U_LIGHT_INTENSITY = "u_LightIntensity";
    protected static final String U_SKELETT_COLOR = "u_SkelettColor";
    protected static final String U_VECTOR_TO_LIGHT = "u_VectorToLight";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    protected static final String A_NORMAL = "a_Normal";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                TextResourceReader
                        .readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader
                        .readTextFileFromResource(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }
}
