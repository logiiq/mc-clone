#version 400 core
layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec2 texturePos;
layout (location = 2) in float lightMul;

uniform mat4 pvm;

out vec2 texPos;
out float lightValue;

void main()
{
    gl_Position = pvm * vec4(vertexPos, 1.0f);
    texPos = texturePos;
    lightValue = lightMul;
}