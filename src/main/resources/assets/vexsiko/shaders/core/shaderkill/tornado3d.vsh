#version 150
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

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

out vec3 vLocal;
out vec3 vNormal;
out vec3 vViewPos;
// float вместо flat int: все 3 вершины треугольника несут одно значение,
// интерполяция константы даёт константу — безопасно на любых драйверах.
out float vPart;
out float vHeight;

const float PI  = 3.14159265359;
const float TAU = 6.28318530718;

const int SHELL_SEG = 24;
const int SHELL_RINGS = 10;
const int SHELL_VERTS = 1296;

const int CORE_SEG = 12;
const int CORE_RINGS = 6;
const int CORE_VERTS = 360;

const int TORUS_SEG = 24;
const int TORUS_TUBE = 4;
const int TORUS_VERTS = 576;

const int ORB_SEG = 12;
const int ORB_RINGS = 6;
const int ORB_VERTS = 360;

const int TOTAL_VERTS = 2592;

float progressEase(float x) {
    x = clamp(x, 0.0, 1.0);
    return x * x * (3.0 - 2.0 * x);
}

vec3 transformWorld(vec3 p) {
    vec3 center = worldPosSize.xyz;
    vec3 world = center + p;
    vec4 view = ModelViewMat * vec4(world, 1.0);
    vViewPos = view.xyz;
    return world;
}

void shellVertex(int id, out vec3 p, out vec3 n) {
    int tri = id / 3;
    int cell = tri / 2;
    int triSide = tri % 2;
    int cornerId = id % 3;

    int corner;
    if (triSide == 0) {
        corner = cornerId;
    } else {
        corner = (cornerId == 0) ? 0 : ((cornerId == 1) ? 2 : 3);
    }

    int seg = cell % SHELL_SEG;
    int ring = cell / SHELL_SEG;

    float u0 = float(seg) / float(SHELL_SEG);
    float u1 = float(seg + 1) / float(SHELL_SEG);
    float v0 = float(ring) / float(SHELL_RINGS - 1);
    float v1 = float(ring + 1) / float(SHELL_RINGS - 1);

    float u = (corner == 1 || corner == 2) ? u1 : u0;
    float v = (corner >= 2) ? v1 : v0;

    float rise = progressEase(smoothstep(0.0, 0.18, uProgress));
    float y = v * worldHeight;

    float radius = mix(0.12, 0.62 * worldPosSize.w, pow(v, 0.78));
    radius *= mix(0.35, 1.0, rise);

    float twist = uTime * (1.55 + 0.35 * uSize) + v * 4.5;
    float wave = 1.0
        + 0.08 * sin(v * 12.0 - uTime * 3.0)
        + 0.035 * sin(v * 27.0 + uTime * 5.0);

    float angle = u * TAU + twist;
    p = vec3(cos(angle) * radius * wave,
             y,
             sin(angle) * radius * wave);

    // Approximate outward normal from the procedural surface.
    n = normalize(vec3(cos(angle), 0.08 + 0.15 * sin(v * PI), sin(angle)));
}

void coreVertex(int id, out vec3 p, out vec3 n) {
    int tri = id / 3;
    int cell = tri / 2;
    int triSide = tri % 2;
    int cornerId = id % 3;

    int corner;
    if (triSide == 0) corner = cornerId;
    else corner = (cornerId == 0) ? 0 : ((cornerId == 1) ? 2 : 3);

    int seg = cell % CORE_SEG;
    int ring = cell / CORE_SEG;

    float u0 = float(seg) / float(CORE_SEG);
    float u1 = float(seg + 1) / float(CORE_SEG);
    float v0 = float(ring) / float(CORE_RINGS - 1);
    float v1 = float(ring + 1) / float(CORE_RINGS - 1);

    float u = (corner == 1 || corner == 2) ? u1 : u0;
    float v = (corner >= 2) ? v1 : v0;

    float growth = smoothstep(0.0, 0.22, uProgress);
    float h = worldHeight * (0.25 + 0.75 * growth);
    float y = v * h;
    float radius = max(0.025, worldPosSize.w * 0.085) * (0.75 + 0.25 * sin(uTime * 5.0));

    float angle = u * TAU - uTime * 2.8;
    p = vec3(cos(angle) * radius, y, sin(angle) * radius);
    n = normalize(vec3(cos(angle), 0.0, sin(angle)));
}

void torusVertex(int id, out vec3 p, out vec3 n) {
    int tri = id / 3;
    int cell = tri / 2;
    int triSide = tri % 2;
    int cornerId = id % 3;

    int corner;
    if (triSide == 0) corner = cornerId;
    else corner = (cornerId == 0) ? 0 : ((cornerId == 1) ? 2 : 3);

    int seg = cell % TORUS_SEG;
    int tube = cell / TORUS_SEG;

    float u0 = float(seg) / float(TORUS_SEG);
    float u1 = float(seg + 1) / float(TORUS_SEG);
    float v0 = float(tube) / float(TORUS_TUBE);
    float v1 = float(tube + 1) / float(TORUS_TUBE);

    float u = (corner == 1 || corner == 2) ? u1 : u0;
    float v = (corner >= 2) ? v1 : v0;

    float open = smoothstep(0.0, 0.16, uProgress);
    float major = worldPosSize.w * mix(0.18, 0.62, open);
    float minor = max(0.018, worldPosSize.w * 0.028);

    float a = u * TAU + uTime * 0.9;
    float b = v * TAU;

    float rr = major + minor * cos(b);
    p = vec3(cos(a) * rr,
             minor * sin(b) + 0.035,
             sin(a) * rr);

    n = normalize(vec3(cos(a) * cos(b), sin(b), sin(a) * cos(b)));
}

void orbVertex(int id, out vec3 p, out vec3 n) {
    int tri = id / 3;
    int cell = tri / 2;
    int triSide = tri % 2;
    int cornerId = id % 3;

    int corner;
    if (triSide == 0) corner = cornerId;
    else corner = (cornerId == 0) ? 0 : ((cornerId == 1) ? 2 : 3);

    int seg = cell % ORB_SEG;
    int ring = cell / ORB_SEG;

    float u0 = float(seg) / float(ORB_SEG);
    float u1 = float(seg + 1) / float(ORB_SEG);
    float v0 = float(ring) / float(ORB_RINGS - 1);
    float v1 = float(ring + 1) / float(ORB_RINGS - 1);

    float u = (corner == 1 || corner == 2) ? u1 : u0;
    float v = (corner >= 2) ? v1 : v0;

    float grow = smoothstep(0.18, 0.48, uProgress);
    float r = worldPosSize.w * (0.08 + 0.12 * grow);
    float cy = worldHeight * (0.82 + 0.08 * grow);

    float phi = (v - 0.5) * PI;
    float theta = u * TAU + uTime * 0.65;

    vec3 dir = vec3(cos(phi) * cos(theta),
                    sin(phi),
                    cos(phi) * sin(theta));

    p = dir * r + vec3(0.0, cy, 0.0);
    n = dir;
}

void main() {
    int id = gl_VertexID;

    vec3 p;
    vec3 n;
    float part;

    if (id < SHELL_VERTS) {
        shellVertex(id, p, n);
        part = 0.0;
    } else if (id < SHELL_VERTS + CORE_VERTS) {
        coreVertex(id - SHELL_VERTS, p, n);
        part = 1.0;
    } else if (id < SHELL_VERTS + CORE_VERTS + TORUS_VERTS) {
        torusVertex(id - SHELL_VERTS - CORE_VERTS, p, n);
        part = 2.0;
    } else {
        orbVertex(id - SHELL_VERTS - CORE_VERTS - TORUS_VERTS, p, n);
        part = 3.0;
    }

    // Animation envelope: grow from the floor and fade out at the end.
    float grow = smoothstep(0.0, 0.18, uProgress);
    float visibleHeight = mix(0.12, worldHeight, grow);

    if (part == 0 && p.y > visibleHeight) {
        // Keep the primitive valid but collapse hidden vertices at the base.
        p.y = visibleHeight;
    }

    vLocal = p;
    vNormal = n;
    vPart = part;
    vHeight = worldHeight;

    vec3 world = transformWorld(p);
    gl_Position = ProjMat * ModelViewMat * vec4(world, 1.0);
}
