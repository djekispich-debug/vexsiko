#version 330

layout(std140) uniform ShaderKillData {
    vec4 rect;
    vec4 screen;
    vec4 uColor;
    float uTime;
    float uProgress;
    float uIntensity;
    float uSize;
    vec4 worldPosSize;
    float worldHeight;
};

in vec2 texCoord;
in vec4 vertexColor;

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
    return mix(
        mix(hash21(i), hash21(i + vec2(1.0,0.0)), f.x),
        mix(hash21(i + vec2(0.0,1.0)), hash21(i + vec2(1.0,1.0)), f.x),
        f.y
    );
}

float fbm2D(vec2 p, int octaves) {
    float v = 0.0, a = 0.5, freq = 1.0;
    for (int k = 0; k < octaves; k++) {
        v += a * smoothNoise2D(p * freq);
        freq *= 2.03;
        a *= 0.48;
    }
    return v;
}

// красивее: палитра с HDR
vec3 pal(vec3 base, float t) {
    vec3 c1 = base;
    vec3 c2 = mix(base, vec3(0.55, 0.75, 1.0), 0.55);
    vec3 c3 = mix(base, vec3(1.0, 0.85, 0.45), 0.35);
    if (t < 0.5) return mix(c1, c2, t*2.0);
    return mix(c2, c3, (t-0.5)*2.0);
}

void main() {
    if (texCoord.y < 0.0) discard;

    float x = texCoord.x * 2.0 - 1.0;
    float y = texCoord.y; // 0..1 bottom->top
    float t = uTime;
    float prog = clamp(uProgress, 0.0, 1.0);
    float sizeScale = clamp(uSize, 0.5, 2.5);

    // --- анимация роста: плавный подъем ---
    float appear = smoothstep(0.0, 0.12, prog);
    float vanish = 1.0 - smoothstep(0.62, 1.0, prog);
    float rise = 0.08 + prog * 0.92;
    float growing = 1.0 - smoothstep(rise - 0.14, rise, y);
    float topFade = 1.0 - smoothstep(0.52, 0.98, y);
    float baseFade = smoothstep(0.0, 0.045, y);
    float body = growing * topFade * baseFade * appear * vanish;
    if (body <= 0.002) discard;

    // --- форма торнадо: узкий низ, расширение к верху ---
    float yPow = pow(y, 1.35);
    float radius = mix(0.10, 0.92, yPow);
    // основание растекается по блоку красиво
    float dustRing = smoothstep(0.0, 0.18, y) * 0.0 + (1.0 - smoothstep(0.0, 0.22, y)) * 0.28 * appear * (0.7 + 0.3*sin(t*1.8));
    radius += dustRing;
    // легкое колебание на ветру
    radius *= (1.0 + 0.04 * sin(y*9.0 - t*1.6) * (1.0 - y*0.6));
    float r = abs(x) / max(radius, 1e-4);

    // стенки торнадо — двойная спираль
    float wallA = 1.0 - smoothstep(0.0, 1.0, abs(r - 0.68) * 2.4);
    float wallB = 1.0 - smoothstep(0.0, 1.0, abs(r - 0.38) * 3.0);
    float wall = max(wallA * 0.95, wallB * 0.42 * (0.6 + 0.4* sin(y*12.0 - t*2.0)));

    // закрутка текстур
    float twist = t * 1.35 * (0.8 + 0.4*sizeScale);
    vec2 sw1 = vec2(x * 2.8, y * 6.5 - twist * 0.9);
    vec2 sw2 = vec2(x * 4.2, y * 9.0 - twist * 1.2);
    float n1 = fbm2D(sw1 + vec2(twist*0.15, 0.0), 4);
    float n2 = fbm2D(sw2 - vec2(twist*0.10, 0.0), 3);
    float twirl = n1 * 0.65 + n2 * 0.35;
    float spiral = smoothstep(0.28, 0.78, twirl + 0.22 * sin(x*7.0 + y*13.0 - t*4.2));
    // вторая полоса для объема
    float spiral2 = smoothstep(0.45, 0.85, n2 + 0.18 * sin(x*11.0 - y*8.0 + t*3.0));
    spiral = mix(spiral, spiral*0.7 + spiral2*0.45, 0.35);

    // ядро — яркий столб в центре
    float coreW = 0.13 + 0.02* sin(t*1.9);
    float core = exp(-(x*x) / (2.0*coreW*coreW)) * smoothstep(0.0, 0.12, y) * (0.9 + 0.2*sin(t*2.2));
    float coreHi = exp(-(x*x) / (2.0*0.045*0.045)) * smoothstep(0.0, 0.10, y) * 0.7;

    // частицы-спираль — больше и виднее
    float particles = 0.0;
    for (int i = 0; i < 22; i++) {
        float seed = float(i)*1.37 + 3.1;
        float hgt = fract(t*0.42 + hash21(vec2(seed, 0.0)) * 0.85);
        if (hgt > rise - 0.06) continue;
        float lifeFade = 1.0 - smoothstep(0.0, 0.12, abs(hgt - (rise-0.07)) * 6.0)*0.5;
        float ang = (hgt*7.0 + t*2.8) + seed*2.1;
        float rad = 0.42 + 0.28 * hash21(vec2(seed, 2.0)) + hgt*0.18;
        float px = sin(ang) * rad * radius;
        float sz = 0.025 + 0.045 * hash21(vec2(seed, 1.0));
        vec2 dc = vec2((x - px)*1.25, (y - hgt)*2.4);
        float d2 = dot(dc,dc);
        particles += exp(-d2 / (2.0*sz*sz)) * 0.85 * lifeFade;
    }

    // свечение основания — пыльное кольцо
    float baseGlow = exp(-pow(max(0.0, y)*8.0, 1.2)) * 0.55 * (0.8 + 0.2*sin(t*2.8));
    baseGlow *= exp(-abs(x)*3.2) * 1.2;
    // общий ореол
    float halo = exp(-abs(x)*2.1) * 0.32 * (0.65 + 0.35*sin(t*1.7 + y*4.0)) * smoothstep(0.0, 0.18, y);

    // --- цвет: используем uColor как базу, делаем HDR ---
    vec3 base = pow(uColor.rgb, vec3(0.95)); // чуть насыщеннее
    base = clamp(base, 0.0, 1.0);
    // градиент по высоте: низ темнее, верх светлее
    vec3 lowCol  = mix(base, base*0.65 + vec3(0.05), 0.35);
    vec3 midCol  = pal(base, 0.35);
    vec3 topCol  = mix(pal(base, 0.78), vec3(1.0), 0.32);
    vec3 yGrad = mix(mix(lowCol, midCol, smoothstep(0.0, 0.45, y)), topCol, smoothstep(0.45, 1.0, y));

    // итоговый цвет
    vec3 colWall = yGrad * (0.95 + 0.35*spiral) * wall;
    vec3 colCore = vec3(1.0, 0.97, 0.94) * core * 1.35 + vec3(1.0)*coreHi*1.2;
    vec3 colPart = mix(yGrad, vec3(1.0, 0.95, 0.8), 0.45) * particles * 0.95;
    vec3 colGlow = mix(base, vec3(0.75, 0.85, 1.0), 0.42) * (baseGlow*1.1 + halo*0.75);

    vec3 finalColor = colWall * 1.35 + colCore + colPart + colGlow;
    // легкий bloom у наития
    finalColor += yGrad * halo * 0.25;

    // альфа: все слои вносят вклад, тело ограничивает
    float fin = (wall * spiral * 0.85 + core*0.95 + coreHi*0.6 + particles*0.55 + baseGlow*0.45 + halo*0.35) * body;
    float alpha = clamp(fin * 1.45, 0.0, 1.0) * vertexColor.a;
    alpha *= uIntensity;

    // мягкие края без серого квадрата
    float edgeX = 1.0 - smoothstep(0.78, 1.0, abs(x) / max(radius*1.25, 0.01));
    float edgeY = 1.0 - smoothstep(0.92, 1.0, y);
    alpha *= edgeX * edgeY;
    // пыльный фейд внизу чтобы не резало
    float bottomSoft = smoothstep(0.0, 0.06, y);
    alpha *= mix(bottomSoft, 1.0, 0.6);

    if (alpha < 0.02) discard;
    // HDR тонмап
    finalColor = clamp(finalColor * uIntensity * (0.95 + 0.25*appear), 0.0, 1.0);
    fragColor = vec4(finalColor, alpha);
}
