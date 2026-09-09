#version 330 core

in vec4 vertexColor;
in vec2 vUV;

uniform float Time;
uniform float CloudSpeed;
uniform float CloudScale;
uniform float Alpha;

out vec4 fragColor;

#define PI2 6.28318530718
#define PI 3.14159265359

float vorocloud(vec2 p, float shaderTime) {
    vec2 pp = cos(vec2(p.x * 14.0, 16.0 * p.y + cos(floor(p.x * 30.0)) + shaderTime * PI2));
    p = cos(p * 12.1 + pp * 10.0 + 0.5 * cos(pp.x * 10.0));

    vec2 pts[4];
    pts[0] = vec2(0.5, 0.6);
    pts[1] = vec2(-0.4, 0.4);
    pts[2] = vec2(0.2, -0.7);
    pts[3] = vec2(-0.3, -0.4);

    float d = 5.0;
    for (int i = 0; i < 4; i++) {
        pts[i].x += 0.03 * cos(float(i)) + p.x;
        pts[i].y += 0.03 * sin(float(i)) + p.y;
        d = min(d, distance(pts[i], pp));
    }

    float f = 2.0 * pow(max(1.0 - 0.3 * d, 0.0), 13.0);
    return min(f, 1.0);
}

vec4 scene(vec2 uv, float shaderTime) {
    vec2 p = (uv - vec2(0.5)) * 1.8;
    vec4 col = vec4(0.0);
    col.g += 0.02;

    float v = vorocloud(p, shaderTime);
    v = 0.2 * floor(v * 5.0);
    col.r += 0.1 * v;
    col.g += 0.6 * v;
    col.b += 0.5 * pow(v, 5.0);

    v = vorocloud(p * 2.0, shaderTime);
    v = 0.2 * floor(v * 5.0);
    col.r += 0.1 * v;
    col.g += 0.2 * v;
    col.b += 0.01 * pow(v, 5.0);

    col.a = 1.0;
    return col;
}

void main() {
    float shaderTime = Time / max(CloudSpeed, 0.001);
    vec2 uv = 0.5 + (vUV - 0.5) / max(CloudScale * 0.35, 0.001);

    fragColor = scene(uv, shaderTime);
    fragColor.a *= Alpha * vertexColor.a;
}
