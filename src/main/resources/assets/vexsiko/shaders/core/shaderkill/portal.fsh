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

in vec2 texCoord;
in vec4 vertexColor;
in vec4 clipPos;

out vec4 fragColor;

float hash21(vec2 p) {
    p = fract(p * vec2(443.8975, 397.2973));
    p += dot(p, p + 19.19);
    return fract(p.x * p.y);
}

float smoothNoise2D(vec2 p) {
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
    float v = 0.0, a = 0.5, freq = 1.0;
    for (int k = 0; k < 4; k++) {
        v += a * smoothNoise2D(p * freq);
        freq *= 2.1;
        a *= 0.48;
    }
    return v;
}

void main() {
    vec2 center = vec2(0.5);
    vec2 uv = texCoord;
    vec2 dir = uv - center;
    float dist = length(dir);
    float angle = atan(dir.y, dir.x);

    float progress = clamp(uProgress, 0.0, 1.0);
    float time = uTime;
    float sizeScale = clamp(uSize, 0.5, 2.5);

    float appear = smoothstep(0.0, 0.10, progress);
    float vanish = 1.0 - smoothstep(0.68, 1.0, progress);
    vanish = vanish * vanish * (3.0 - 2.0*vanish);
    float env = appear * vanish;

    // радиус портала: быстро раскрывается, пульсирует
    float radius = 0.06 + 0.44 * smoothstep(0.0, 1.0, progress);
    radius *= (0.92 + 0.10 * sin(time*2.6 + progress*6.0));
    radius *= (0.18 + 0.82 * vanish + 0.12*env);
    radius = max(radius, 0.001);
    // эллипс учет ширины/высоты: texCoord квадрат, но в world билборд может быть шире — компенсируем мягко
    // визуально делаем сферу, искажение минимально

    // 3D сфера для глубины — вращаем
    float phi = dist * 3.14159 * (1.0 + 0.06*sizeScale);
    float theta = angle;
    float sx = sin(phi) * cos(theta);
    float sy = sin(phi) * sin(theta);
    float sz = cos(phi);
    float rotAngle = time * 0.55 + progress*0.4;
    float cx = sx * cos(rotAngle) - sz * sin(rotAngle);
    float cz = sx * sin(rotAngle) + sz * cos(rotAngle);
    sx = cx; sz = cz;

    float sphereMask = 1.0 - smoothstep(0.0, radius, dist);
    // мягкая граница сферы — объемное затухание
    float rim = pow(clamp(1.0 - abs(dist - radius*0.88)/ (radius*0.35), 0.0, 1.0), 1.6);
    float facing = clamp(sz*0.5 + 0.5, 0.0, 1.0);

    // 1. сетка энергетическая — более контрастная
    float lat = sin(phi * 9.0 + time*0.9) * 0.5 + 0.5;
    float lon = sin(theta * 7.0 + time*1.1) * 0.5 + 0.5;
    float grid = 0.0;
    float latLine = smoothstep(0.975, 0.985, lat) + smoothstep(0.985, 0.975, lat)*0.0;
    float lonLine = smoothstep(0.975, 0.985, lon);
    // делаем сетку переменной толщины
    grid = max( (lat > 0.965 ? 1.0 - smoothstep(0.965, 0.985, lat)*0.0 : 0.0),
                (lon > 0.968 ? 1.0 : 0.0) );
    // проще: порог
    grid = (lat > 0.97 || lon > 0.97) ? 1.0 : 0.0;
    grid *= sphereMask * (0.55 + 0.45 * facing) * (1.15 + 0.25*sin(time*1.3));
    grid *= 1.45;

    // шум по сфере для энергетических прожилок
    float noiseSphere = fbm(dir*3.2 + time*0.18);
    float veins = smoothstep(0.58, 0.82, noiseSphere) * sphereMask * facing * 0.65;

    // 2. кольца — 5 колец, более видные
    float rings = 0.0;
    for (int i = 0; i < 5; i++) {
        float fi = float(i);
        float baseR = 0.19 + fi * 0.11 + 0.045 * sin(time*0.45 + fi*1.4);
        // эллиптичность легкая
        float ringDist = length(dir * vec2(1.0, 0.96 + 0.04*sin(fi)));
        float ringW = 0.009 + 0.008 * (1.0 - progress) + 0.004*sizeScale;
        float ring = 1.0 - smoothstep(baseR - ringW, baseR + ringW, ringDist);
        ring *= (1.0 - abs(ringDist - baseR) * 5.0);
        ring *= (0.65 + 0.35 * sin(time*1.25 + fi*1.15));
        ring *= sphereMask * (0.45 + 0.55 * facing);
        // мерцание
        ring *= (0.9 + 0.25 * sin(time*2.0 + fi));
        rings += ring * (0.85 - fi*0.07);
    }
    rings *= 1.9;

    // 3. молнии — более яркие, меньше но виднее
    float lightning = 0.0;
    for (int i = 0; i < 7; i++) {
        float seed = float(i)*1.73 + 0.7;
        float a = hash21(vec2(seed, 0.0)) * 6.2832 + time*0.35;
        float len = 0.35 + 0.65 * hash21(vec2(seed, 1.0));
        float life = fract(time * 0.95 + seed*0.55);
        float bright = 1.0 - smoothstep(0.0, 0.85, life);
        bright = pow(bright, 0.9) * (0.7 + 0.3*sin(life*12.0));
        float rad = radius * (0.18 + 0.82*len);
        vec2 p = vec2(0.5 + rad * cos(a), 0.5 + rad * sin(a));
        vec2 delta = uv - p;
        vec2 dirToP = normalize(p - center + vec2(1e-5));
        vec2 perp = vec2(-dirToP.y, dirToP.x);
        float proj = dot(delta, dirToP);
        float perpDist = abs(dot(delta, perp));
        float line = 1.0 - smoothstep(0.0, 0.018, perpDist);
        // зигзаг
        float zig = 0.008 * sin(proj*55.0 + seed*6.0 + time*6.0);
        line *= 1.0 - smoothstep(0.0, 0.015, abs(perpDist - zig)*2.0)*0.4;
        line *= smoothstep(0.0, 0.07, proj) * smoothstep(1.0, 0.82, proj/(rad+0.001));
        lightning += line * bright * 1.35;
    }
    lightning *= sphereMask * (0.5 + 0.5*facing);
    lightning *= 1.6;

    // 4. ядро — более HDR, пульс
    float pulse = 1.0 + 0.22 * sin(time*3.2) + 0.12*sin(time*5.7);
    float coreDist = length(dir) / (0.028 + 0.016*progress);
    float core = exp(-coreDist*coreDist*0.9) * 2.8 * pulse;
    core *= (1.0 - progress*0.18);
    core *= (0.62 + 0.38 * facing);
    float coreRing = exp(-pow((dist - 0.055)*18.0, 2.0)) * 0.9 * env;

    // 5. частицы — 48, более объемные
    float particles = 0.0;
    for (int i = 0; i < 48; i++) {
        float seed = float(i)*1.41;
        float a = hash21(vec2(seed, 0.0)) * 6.2832;
        float rr = radius * (0.12 + 0.88 * hash21(vec2(seed, 1.0)));
        float life = fract(time*0.62 + seed*0.31);
        float bright = pow(1.0 - smoothstep(0.0, 0.88, life), 1.1);
        float angleOff = time * (0.45 + 0.55*hash21(vec2(seed, 2.0)));
        float phiP = (rr / max(radius,0.001)) * 3.14159 * 0.92;
        float thetaP = a + angleOff;
        float sxP = sin(phiP) * cos(thetaP);
        float syP = sin(phiP) * sin(thetaP);
        float szP = cos(phiP);
        vec2 p = center + vec2(sxP, syP) * radius * 0.52;
        float front = 0.45 + 0.55 * (szP*0.5+0.5);
        if (front < 0.22) continue;
        float sz2 = 0.007 + 0.022 * hash21(vec2(seed, 3.0)) * sizeScale;
        float d = length(uv - p);
        float g = exp(-d*d / (2.0*sz2*sz2));
        particles += g * bright * 0.95 * front;
    }
    particles *= (1.0 - progress*0.35);
    particles *= 1.55;

    // 6. свечение + внешний ореол
    float glow = exp(-dist*4.2) * 0.72 * (1.0 + 0.28*sin(time*2.4)) * env;
    glow *= (1.0 - progress*0.22);
    glow *= (0.58 + 0.42 * facing) * sphereMask * 1.1;
    float bloom = exp(-dist*2.5) * 0.42 * sphereMask * env;
    float outerGlow = exp(-pow(max(dist - radius, 0.0)*6.5, 1.1)) * 0.32 * env * (0.7 + 0.3*facing);

    // 7. расширяющееся кольцо ударной волны
    float ring2 = 0.0;
    float r2 = radius * (1.25 + 0.75*progress);
    float ringW2 = 0.022 + 0.015*(1.0-progress);
    ring2 = 1.0 - smoothstep(r2 - ringW2, r2 + ringW2, dist);
    ring2 *= 0.55 * (1.0 - progress) * sin(progress*3.14159) * env;
    ring2 *= 1.35;

    // --- цвета: beamColor как база ---
    vec3 base = pow(beamColor.rgb, vec3(0.95));
    base = clamp(base, 0.0, 1.0);
    vec3 baseBright = mix(base, vec3(1.0), 0.18);
    vec3 gridCol = mix(baseBright, vec3(0.88, 0.96, 1.0), 0.42);
    vec3 ringCol = mix(base, vec3(1.0, 0.88, 0.42), 0.55);
    vec3 veinCol = mix(base, vec3(0.65, 0.85, 1.0), 0.5);
    vec3 lightningCol = vec3(0.92, 0.96, 1.0);
    vec3 coreCol = vec3(1.0, 0.99, 0.93);
    vec3 partCol = mix(base, vec3(1.0, 0.9, 0.55), 0.45);
    vec3 glowCol = mix(base, vec3(0.62, 0.82, 1.0), 0.48);

    float veinMask = veins;
    float finalIntensity = grid + rings + lightning + core + particles + glow*0.9 + bloom*0.7 + outerGlow*0.8 + veinMask*0.6 + ring2 + coreRing;

    vec3 finalColor = gridCol * grid * 1.55
                    + ringCol * rings * 1.35
                    + veinCol * veins * 0.85
                    + lightningCol * lightning * 1.25
                    + coreCol * (core*2.6 + coreRing*1.4)
                    + partCol * particles * 1.05
                    + glowCol * glow * 0.75
                    + vec3(0.78, 0.88, 1.0) * bloom * 0.62
                    + vec3(0.65, 0.82, 1.0) * outerGlow * 0.55
                    + base * ring2 * 0.7;
    finalColor *= uIntensity * (1.15 + 0.25*rim);
    // fresnel rim дополнительно
    finalColor += baseBright * rim * 0.28 * sphereMask * env * uIntensity;

    float finalAlpha = clamp(finalIntensity * 1.55, 0.0, 1.0) * vertexColor.a;
    float vignette = 1.0 - dist * 0.32;
    finalAlpha *= mix(vignette, 1.0, 0.45);
    finalAlpha *= env;
    // мягкая граница сферы — убираем квадрат
    finalAlpha *= sphereMask;
    float edgeFade = 1.0 - smoothstep(radius*0.82, radius*1.06, dist);
    finalAlpha *= edgeFade;
    // внешний ореол тоже полупрозрачный
    float outerAlpha = clamp(outerGlow*1.8 + bloom*0.9, 0.0, 1.0) * 0.35 * env;
    finalAlpha = max(finalAlpha, outerAlpha);

    if (finalAlpha < 0.025) discard;
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), finalAlpha);
}
