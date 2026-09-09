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

const int MIRROR_STEPS = 36;

float map(vec3 p) {
    p = abs(fract(p) - 0.5);
    return abs(min(length(p.xy) - 0.175, min(p.x, p.y) + 1e-3)) + 1e-3;
}

vec3 estimateNormal(vec3 p) {
    const float eps = 0.0025;
    return normalize(vec3(
        map(p + vec3(eps, 0.0, 0.0)) - map(p - vec3(eps, 0.0, 0.0)),
        map(p + vec3(0.0, eps, 0.0)) - map(p - vec3(0.0, eps, 0.0)),
        map(p + vec3(0.0, 0.0, eps)) - map(p - vec3(0.0, 0.0, eps))
    ));
}

vec3 themeGradient(float t) {
    t = clamp(t, 0.0, 1.0);
    if (t < 0.3333) {
        return mix(Color1, Color2, t / 0.3333);
    }
    if (t < 0.6666) {
        return mix(Color2, Color3, (t - 0.3333) / 0.3333);
    }
    return mix(Color3, Color4, (t - 0.6666) / 0.3334);
}

void main() {
    float shaderTime = Time / max(CloudSpeed, 0.001);
    vec2 uv = 0.5 + (vUV - 0.5) / max(CloudScale * 0.35, 0.001);
    vec2 centeredUv = (uv - 0.5) * 2.0;

    float z = fract(dot(vUV * 341.73 + shaderTime, sin(vUV.yx * 197.31 + shaderTime))) - 0.5;
    vec3 accumulated = vec3(0.0);

    for (int i = 0; i < MIRROR_STEPS; i++) {
        vec4 p = vec4(z * normalize(vec3(centeredUv, 1.15)), 0.1 * shaderTime);
        p.z += shaderTime;

        vec4 q = p;
        vec4 rotA = cos(2.0 + q.z + vec4(0.0, 11.0, 33.0, 0.0));
        vec4 rotB = cos(q + vec4(0.0, 11.0, 33.0, 0.0));
        p.xy *= mat2(rotA.x, rotA.y, rotA.z, rotA.w);
        p.xy *= mat2(rotB.x, rotB.y, rotB.z, rotB.w);

        float d = map(p.xyz);
        vec3 normal = estimateNormal(p.xyz);
        vec3 viewDir = normalize(vec3(centeredUv, 1.0));
        vec3 reflectDir = reflect(viewDir, normal);
        vec3 lightDir = normalize(vec3(0.3, 0.5, 1.0));

        float gradientT = clamp(0.5 + 0.5 * reflectDir.y, 0.0, 1.0);
        vec3 envColor = mix(vec3(0.82, 0.42, 0.88), vec3(1.0), gradientT);
        envColor *= mix(vec3(1.0), themeGradient(gradientT), 0.55);

        float spec = pow(max(dot(reflectDir, lightDir), 0.0), 32.0);
        vec4 baseColor = (1.0 + sin(0.5 * q.z + length(p.xyz - q.xyz) + vec4(0.0, 4.0, 3.0, 6.0)))
                / (0.5 + 2.0 * dot(q.xy, q.xy));
        vec3 accent = themeGradient(fract(length(p.xyz - q.xyz) + float(i) * 0.11 + shaderTime * 0.15));
        vec3 finalColor = baseColor.rgb * 0.1 + mix(envColor, accent, 0.25) * 0.9 + vec3(spec) * 1.2;

        accumulated += finalColor / max(d, 1e-4);
        z += 0.6 * d;
    }

    vec3 mirror = tanh(accumulated / 1.6e4) * vertexColor.rgb;
    fragColor = vec4(mirror, Alpha * vertexColor.a);
}
