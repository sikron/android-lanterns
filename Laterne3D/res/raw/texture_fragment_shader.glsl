#ifdef GL_FRAGMENT_PRECISION_HIGH
precision highp float;
#else
precision mediump float;
#endif

uniform mat4 u_MVMatrix;
uniform mat4 u_IT_MVMatrix;

uniform vec4 u_PointLightPosition;    // In model space
uniform vec3 u_PointLightColor;

uniform float u_LightIntensity;
uniform vec3 u_SkelettColor;
uniform vec3 u_VectorToLight;             // In eye space

uniform sampler2D u_TextureUnit;

//interpolated from vertex shader
varying vec2 v_TextureCoordinates;
varying vec3 v_Normal;
varying vec4 v_Position;

vec4 textureColor;
vec4 color;
vec4 eyeSpacePosition;
vec3 eyeSpaceNormal;


//methods
vec4 getAmbientLighting();
vec4 getCandleLighting();
vec4 getOuterLighting();
bool isSkelettColor(in vec4 color);
float contrast(in float value);
vec4 glow();


void main()
{
    textureColor = texture2D(u_TextureUnit, v_TextureCoordinates);

    /*
    //for see-through of transparent areas of the lantern-texture
    if (textureColor.a < 0.9)
    {
        discard;
    }
    */

    eyeSpacePosition = u_MVMatrix * v_Position;

    // The model normals need to be adjusted as per the transpose of the inverse of the modelview matrix.
    eyeSpaceNormal = vec3(u_IT_MVMatrix * vec4(v_Normal, 0.0));

    //eyeSpaceNormal = normalize(eyeSpaceNormal);
    // normalizing "eyeSpaceNormal" is not necessary, as the model was not scaled
    // (translate & rotate do not concern the normality)

    color = vec4(0.0);
    color += getAmbientLighting();

    if (!isSkelettColor(textureColor)){
    
        color += getCandleLighting() * u_LightIntensity;
        //color += glow() * u_LightIntensity;

    } else {

        color += getOuterLighting();
    }

    gl_FragColor = vec4(color.rgb, textureColor.a);
}

bool isSkelettColor(in vec4 color)
{
    //at least the skeleton, maybe a little of the folie is included
    if ((color.r >= u_SkelettColor.r * 0.9 && color.r <= u_SkelettColor.r * 1.1)
        && (color.g >= u_SkelettColor.g * 0.9 && color.g <= u_SkelettColor.g * 1.1)
        && (color.b >= u_SkelettColor.b * 0.9 && color.b <= u_SkelettColor.b * 1.1))
    {
        return true;
    }
    else
    {
        return false;
    }

    /*
    if (color.rgb == u_SkelettColor.rgb)
    {
        return true;
    }
    else
    {
        return false;
    }
    */
}

vec4 getAmbientLighting()
{
    return textureColor * 0.3;
}

vec4 getCandleLighting()
{
    //attenuation would be nice

    vec4 pointLightPositionEyeSpace = u_MVMatrix * u_PointLightPosition;
    vec3 toPointLight = vec3(pointLightPositionEyeSpace) - vec3(eyeSpacePosition);
    float distance = length(toPointLight);
    toPointLight = normalize(toPointLight);

    float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);
    float brightness = 1.0 * cosine / distance;
    vec4 lightingSum = textureColor * vec4(u_PointLightColor.rgb, 1.0) * brightness * 1.3;

    return lightingSum;
}

vec4 getOuterLighting()
{
    vec3 outerEyeSpaceNormal = vec3(- eyeSpaceNormal.x, - eyeSpaceNormal.y, - eyeSpaceNormal.z);
    float dotProduct = dot(outerEyeSpaceNormal, normalize(u_VectorToLight));
    float cosine = max(dotProduct, 0.0);
    return textureColor * contrast(cosine) * 1.3;   //so that the difference between dark and light is strong
}

//value e [0,1]; result e [0,1]
float contrast(float value)
{
    return exp(value * 10.0) / 12000.0;
}

vec4 glow()
{
   vec4 sum = vec4(0);
   vec4 texture = texture2D(u_TextureUnit, v_TextureCoordinates);
   int j;
   int i;

   for(i = -4 ; i < 4; i++)
   {
        for (j = -3; j < 3; j++)
        {
            sum += texture2D(u_TextureUnit, v_TextureCoordinates + vec2(j, i) * 0.004) * 0.25;
        }
   }

   if (texture.r < 0.3)
   {
        return sum * sum * 0.012 + texture;
   }
   else
   {
        if (texture.r < 0.5)
        {
            return sum * sum * 0.009 + texture;
        }
        else
        {
            return sum * sum * 0.0075 + texture;
        }
   }
}