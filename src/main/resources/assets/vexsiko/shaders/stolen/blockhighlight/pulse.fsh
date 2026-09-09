#version 330 core

in vec4 vertexColor;
in vec3 vertexPosition;

uniform float time;
uniform float pulseSpeed;

out vec4 fragColor;

void main() {
    float pulse = 0.5 + 0.5 * sin(time * pulseSpeed * 3.0);
    
    vec3 pulseColor = vertexColor.rgb * pulse;
    float alpha = vertexColor.a * (0.7 + 0.3 * pulse);
    
    fragColor = vec4(pulseColor, alpha);
}
