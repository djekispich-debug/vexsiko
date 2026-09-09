#version 330 core

in vec4 vertexColor;
in vec3 vertexPosition;

uniform float time;
uniform float waveSpeed;
uniform float waveScale;

out vec4 fragColor;

void main() {
    vec2 st = vertexPosition.xz / waveScale;
    
    for(float i = 1.0; i < 6.0; i++) {
        st.x += 0.6 / i * cos(i * 2.5 * st.y + time * waveSpeed);
        st.y += 0.6 / i * cos(i * 1.5 * st.x + time * waveSpeed);
    }
    
    vec3 waveColor = vertexColor.rgb * (0.4 + 0.6 * abs(sin(time * waveSpeed - st.y - st.x)));
    
    fragColor = vec4(waveColor, vertexColor.a);
}
