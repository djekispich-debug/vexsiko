#version 330 core

in vec4 vertexColor;
in vec2 vUV;

uniform float Time;
uniform float CloudSpeed;
uniform float CloudScale;
uniform vec3 Color1;
uniform vec3 Color2;
uniform vec3 Color3;
uniform vec3 Color4;
uniform float Alpha;

out vec4 fragColor;

vec3 getGradientColor(float t) {
    t = fract(t);
    if (t < 0.25) {
        return mix(Color1, Color2, t * 4.0);
    } else if (t < 0.5) {
        return mix(Color2, Color3, (t - 0.25) * 4.0);
    } else if (t < 0.75) {
        return mix(Color3, Color4, (t - 0.5) * 4.0);
    } else {
        return mix(Color4, Color1, (t - 0.75) * 4.0);
    }
}

void main() {
    vec2 st = vUV / CloudScale;
    float t = Time / CloudSpeed;
    
    for(float i = 1.0; i < 8.0; i++) {
        st.x += 0.6 / i * cos(i * 2.5 * st.y + t);
        st.y += 0.6 / i * cos(i * 1.5 * st.x + t);
    }
    
    float gradientT = fract(t * 0.1 + length(st) * 0.1);
    vec3 baseColor = getGradientColor(gradientT);
    
    vec3 waveColor = baseColor * 0.1 / abs(sin(t - st.y - st.x));
    waveColor = clamp(waveColor, 0.0, 2.0);
    
    float alpha = Alpha * clamp(length(waveColor) / length(baseColor), 0.3, 1.0);
    
    fragColor = vec4(waveColor, alpha);
}

