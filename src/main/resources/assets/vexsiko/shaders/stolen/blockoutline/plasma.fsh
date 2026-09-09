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

mat2 m(float a) {
    float c = cos(a), s = sin(a);
    return mat2(c, -s, s, c);
}

float map(vec3 p, float t) {
    p.xz *= m(t * 0.4);
    p.xy *= m(t * 0.3);
    vec3 q = p * 2.0 + t;
    return length(p + vec3(sin(t * 0.7))) * log(length(p) + 1.0) + 
           sin(q.x + sin(q.z + sin(q.y))) * 5.5 - 1.0;
}

void main() {
    vec2 fragCoord = vUV / CloudScale;
    vec2 p = fragCoord.xy - vec2(0.9, 0.5);
    
    float t = Time / CloudSpeed;
    
    vec3 cl = vec3(0.0);
    float d = 0.9;
    
    for(int i = 0; i <= 5; i++) {
        vec3 rayPos = vec3(0.0, 0.0, 5.0) + normalize(vec3(p, -1.0)) * d;
        float rz = map(rayPos, t);
        float f = clamp((rz - map(rayPos + 0.1, t)) * 0.5, -0.1, 1.0);
        vec3 l = vec3(0.1, 0.3, 0.4) + vec3(5.0, 2.5, 3.0) * f;
        cl = cl * l + (1.0 - smoothstep(0.0, 2.5, rz)) * 0.7 * l;
        d += min(rz, 1.0);
    }
    
    float gradientT = fract(t * 0.1 + length(p) * 0.1);
    vec3 baseColor = getGradientColor(gradientT);
    
    cl *= baseColor;
    float alpha = Alpha * clamp(length(cl) * 0.5, 0.6, 1.0);
    
    fragColor = vec4(cl, alpha);
}

