uniform mat4 u_MVPMatrix;

attribute vec2 a_TextureCoordinates;
attribute vec3 a_Normal;
attribute vec4 a_Position;

varying vec2 v_TextureCoordinates;
varying vec3 v_Normal;
varying vec4 v_Position;

void main()
{
    v_TextureCoordinates = a_TextureCoordinates;
    v_Normal = a_Normal;
    v_Position = a_Position;

    gl_Position = u_MVPMatrix * v_Position;
}