package com.skronawi.laterne3d.programs;

import android.content.Context;
import com.skronawi.laterne3d.R;

import static android.opengl.GLES20.*;

public class SkyboxShaderProgram extends ShaderProgram {
    private final int uMVPMatrixLocation;
    private final int uTextureUnitLocation;
    private final int aPositionLocation;

    public SkyboxShaderProgram(Context context) {
        super(context, R.raw.skybox_vertex_shader, R.raw.skybox_fragment_shader);

        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] modelViewProjectionMatrix, int textureId) {

        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, modelViewProjectionMatrix, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
