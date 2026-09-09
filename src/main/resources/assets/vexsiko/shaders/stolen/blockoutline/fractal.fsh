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

float hash(float n) {
    return fract(sin(n) * 43.5453);
}

void main() {
    vec2 fragCoord = vUV / CloudScale;
    vec2 p = fragCoord.xy;
    
    float t = Time / CloudSpeed;
    
    vec3 ro = vec3(sin(t * 0.16), 0.0, cos(t * 0.1));
    vec3 ta = ro + vec3(sin(t * 0.15), sin(t * 0.18), cos(t * 0.24));
    float roll = 0.0;
    
    vec3 cw = normalize(ta - ro);
    vec3 cp = vec3(sin(roll), cos(roll), 0.0);
    vec3 cu = normalize(cross(cp, cw));
    vec3 rd = normalize(p.x * cu + p.y * cp + cw * 2.0);
    
    vec3 v = vec3(0.0);
    for (float s = 0.1; s <= 5.0; s += 0.1) {
        vec3 rayPos = ro + rd * s;
        for (float i = 0.1; i < 1.0; i += 0.12) {
            rayPos = abs(rayPos) / dot(rayPos + sin(t * 0.1) * 0.1, rayPos) - 0.5;
            float a = length(rayPos);
            
            float gradientT = fract(t * 0.1 + i + s * 0.1);
            vec3 baseColor = getGradientColor(gradientT);
            
            v += baseColor * vec3(i, i * i, i * i * i) * a * 0.12;
        }
    }
    
    vec3 col = v * 0.01;
    float alpha = Alpha * clamp(length(col) * 2.0, 0.3, 1.0);
    
    fragColor = vec4(col, alpha);
}

