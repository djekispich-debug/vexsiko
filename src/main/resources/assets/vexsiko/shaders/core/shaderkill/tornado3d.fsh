#version 150

layout(std140) uniform ShaderKillData {
    vec4 rect;
    vec4 screen;
    vec4 beamColor;
    float uTime;
    float uProgress;
    float uIntensity;
    float uSize;
    vec4 worldPosSize;
    float worldHeight;
};

in vec3 vLocal;
in vec3 vNormal;
in vec3 vViewPos;
in float vPart;
in float vHeight;

out vec4 fragColor;

float hash21(vec2 p) {
    p = fract(p * vec2(443.8975, 397.2973));
    p += dot(p, p + 19.19);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);

    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));

    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;

    for (int i = 0; i < 4; i++) {
        v += noise(p) * a;
        p = p * 2.05 + 7.13;
        a *= 0.48;
    }

    return v;
}

vec3 palette(vec3 base, float t) {
    vec3 hot = mix(base, vec3(1.0, 0.72, 0.95), 0.35);
    vec3 cold = mix(base, vec3(0.55, 0.75, 1.0), 0.25);
    return mix(cold, hot, clamp(t, 0.0, 1.0));
}

void main() {
    float progress = clamp(uProgress, 0.0, 1.0);
    float time = uTime;

    float appear = smoothstep(0.0, 0.10, progress);
    float disappear = 1.0 - smoothstep(0.68, 1.0, progress);
    disappear *= disappear * (3.0 - 2.0 * disappear);
    float envelope = appear * disappear;

    vec3 base = clamp(beamColor.rgb, 0.0, 1.0);
    vec3 hot = vec3(1.0, 0.96, 1.0);

    if (vPart < 0.5) {
        float y = clamp(vLocal.y / max(vHeight, 0.001), 0.0, 1.0);
        float radius = max(length(vLocal.xz), 0.0001);
        float angle = atan(vLocal.z, vLocal.x);

        float n = fbm(vec2(angle * 2.5 + time * 0.8, y * 7.0 - time * 1.4));

        // Several animated spiral bands on the actual cylindrical surface.
        float wave1 = sin(angle * 5.0 - y * 13.0 + time * 3.3 + n * 3.0);
        float wave2 = sin(angle * 9.0 + y * 21.0 - time * 4.7 - n * 2.0);

        float band1 = smoothstep(0.50, 0.92, wave1 * 0.5 + 0.5);
        float band2 = smoothstep(0.68, 0.96, wave2 * 0.5 + 0.5);

        // Vertical electric filaments.
        float filaments = smoothstep(
            0.88, 0.99,
            sin(angle * 17.0 - y * 8.0 + time * 5.0 + n * 4.0) * 0.5 + 0.5
        );

        // Fresnel makes the volume read as 3D from every angle.
        vec3 N = normalize(vNormal);
        vec3 V = normalize(-vViewPos);
        float fresnel = pow(1.0 - abs(dot(N, V)), 2.2);

        float baseGlow = exp(-radius / max(worldPosSize.w * 0.55, 0.001));
        float topFade = 1.0 - smoothstep(0.88, 1.0, y);
        float bottomFade = smoothstep(0.0, 0.08, y);

        float density =
            band1 * 0.85 +
            band2 * 0.48 +
            filaments * 0.72 +
            fresnel * 0.65 +
            baseGlow * 0.16;

        density *= topFade * bottomFade;
        density *= envelope;

        vec3 col = palette(base, 0.25 + 0.65 * band1);
        col = mix(col, hot, clamp(filaments * 0.75 + fresnel * 0.45, 0.0, 1.0));
        // Градиент по высоте: низ глубже, верх светлее.
        col = mix(col * 0.82 + base * 0.18, mix(col, hot, 0.28), y);

        // Hot vertical core visible through the translucent shell.
        float core = exp(
            -pow(length(vLocal.xz) / max(worldPosSize.w * 0.10, 0.001), 2.0)
        );
        core *= 0.75 + 0.25 * sin(time * 5.0 + y * 10.0);
        core *= envelope;

        col += hot * core * 1.35;

        float alpha = clamp(density * 0.72 * uIntensity, 0.0, 0.96);
        vec3 outCol = col * (1.15 + 0.45 * uIntensity);

        if (alpha < 0.012) discard;
        fragColor = vec4(clamp(outCol, 0.0, 1.0), alpha);
        return;
    }

    if (vPart < 1.5) {
        // Inner luminous core.
        float y = clamp(vLocal.y / max(vHeight, 0.001), 0.0, 1.0);
        float pulse = 1.0 + 0.25 * sin(time * 8.0 + y * 18.0);
        float spiral = 0.5 + 0.5 * sin(time * 5.0 + y * 22.0);

        float alpha = envelope * (0.62 + 0.28 * spiral);
        alpha *= uIntensity;

        vec3 col = mix(base, hot, 0.55) * (2.0 + pulse);
        if (alpha < 0.01) discard;

        fragColor = vec4(clamp(col, 0.0, 1.0), clamp(alpha, 0.0, 0.95));
        return;
    }

    if (vPart < 2.5) {
        // Real 3D torus at the base.
        float pulse = 0.72 + 0.28 * sin(time * 6.0);
        float spark = smoothstep(
            0.60, 0.98,
            sin(atan(vLocal.z, vLocal.x) * 12.0 - time * 4.0) * 0.5 + 0.5
        );

        float alpha = envelope * (0.78 + spark * 0.8) * pulse * uIntensity;
        vec3 col = mix(base, hot, 0.72) * (1.8 + spark * 1.2);

        if (alpha < 0.01) discard;
        fragColor = vec4(clamp(col, 0.0, 1.0), clamp(alpha, 0.0, 1.0));
        return;
    }

    // Floating energy orb.
    vec3 N = normalize(vNormal);
    vec3 V = normalize(-vViewPos);
    float fresnel = pow(1.0 - abs(dot(N, V)), 2.0);
    float n = fbm(vLocal.xz * 18.0 + time * 1.8);
    float veins = smoothstep(0.58, 0.9, n);

    float pulse = 0.8 + 0.25 * sin(time * 7.0);
    float alpha = envelope * (0.55 + fresnel * 0.8 + veins * 0.35);
    alpha *= uIntensity;

    vec3 col = mix(base, hot, 0.6) * (1.7 + fresnel * 1.4) * pulse;
    col += hot * veins * 0.8;

    if (alpha < 0.01) discard;
    fragColor = vec4(clamp(col, 0.0, 1.0), clamp(alpha, 0.0, 0.98));
}
