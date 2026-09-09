#version 330 core

in vec4 vertexColor;
in vec3 vertexPosition;

uniform float time;
uniform float gradientSpeed;

out vec4 fragColor;

void main() {
    float gradient = fract(vertexPosition.y * 2.0 + time * gradientSpeed);
    
    vec3 gradientColor = vertexColor.rgb * (0.3 + 0.7 * gradient);
    
    fragColor = vec4(gradientColor, vertexColor.a);
}
