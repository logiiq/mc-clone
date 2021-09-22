#version 400 core
out vec4 FragColor;

in vec2 texPos;
in float lightValue;

uniform sampler2D sampler;

void main()
{
    vec4 light = vec4(lightValue, lightValue, lightValue, 1.0);
    FragColor = light * texture(sampler, texPos);
}