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
    vec2 fragCoord = vUV / CloudScale;
    
    float t = Time / CloudSpeed + 5.0;
    float z = 6.0;
    const int n = 100;
    
    vec3 startColor = vec3(0.0, 0.64, 0.2);
    vec3 endColor = vec3(0.06, 0.35, 0.85);
    float startRadius = 0.84;
    float endRadius = 1.6;
    float power = 0.51;
    float duration = 4.0;
    
    vec2 v = z * fragCoord;
    
    vec3 col = vec3(0.0);
    float mbRadius = 0.0;
    float sum = 0.0;
    
    float evo = (sin(Time / CloudSpeed * 0.01 + 400.0) * 0.5 + 0.5) * 99.0 + 1.0;
    
    for(int i = 0; i < n; i++) {
        float d = fract(t * power + 48934.4238 * sin(float(i / int(evo)) * 692.7398));
        float a = 6.28 * float(i) / float(n);
        float x = d * cos(a) * duration;
        float y = d * sin(a) * duration;
        float distRatio = d / duration;
        
        mbRadius = mix(startRadius, endRadius, distRatio);
        vec2 p = v - vec2(x, y);
        float mb = mbRadius / dot(p, p);
        sum += mb;
        
        float gradientT = fract(t * 0.1 + distRatio);
        vec3 particleColor = getGradientColor(gradientT);
        
        col = mix(col, particleColor, mb / sum);
    }
    
    sum /= float(n);
    col = normalize(col) * sum;
    sum = clamp(sum, 0.0, 0.4);
    
    vec3 tex = vec3(1.0);
    col *= smoothstep(tex, vec3(0.0), vec3(sum));
    
    float alpha = Alpha * clamp(length(col) * 2.0, 0.6, 1.0);
    
    fragColor = vec4(col, alpha);
}

