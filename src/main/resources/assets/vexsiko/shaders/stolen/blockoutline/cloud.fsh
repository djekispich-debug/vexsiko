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

float rand(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float noise(vec2 p) {
    vec2 ip = floor(p);
    vec2 u = fract(p);
    u = u * u * (3.0 - 2.0 * u);
    
    float res = mix(
        mix(rand(ip), rand(ip + vec2(1.0, 0.0)), u.x),
        mix(rand(ip + vec2(0.0, 1.0)), rand(ip + vec2(1.0, 1.0)), u.x),
        u.y
    );
    return res * res;
}

const mat2 mtx = mat2(0.80, 0.60, -0.60, 0.80);

float fbm(vec2 p) {
    float f = 0.0;
    f += 0.500000 * noise(p + Time / CloudSpeed); p = mtx * p * 2.02;
    f += 0.250000 * noise(p); p = mtx * p * 2.03;
    f += 0.125000 * noise(p); p = mtx * p * 2.01;
    f += 0.062500 * noise(p);
    return f / 0.9375;
}

void main() {
    vec2 uv = vUV / CloudScale;
    float pattern = fbm(uv + fbm(uv));
    
    float gradientT = fract(Time / CloudSpeed * 0.1 + pattern);
    vec3 baseColor = getGradientColor(gradientT);
    
    vec3 cloudColor = baseColor * (0.5 + pattern * 0.5);
    float alpha = Alpha * (0.7 + pattern * 0.3);
    
    fragColor = vec4(cloudColor, alpha);
}

