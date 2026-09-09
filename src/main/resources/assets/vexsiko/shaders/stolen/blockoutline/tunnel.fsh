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

#define NEAR 0.0
#define FAR 50.0
#define MAX_STEPS 64
#define PI 3.14159265359
#define EPS 0.001

float hash(vec2 p) {
    float h = 1.0 + dot(p, vec2(127.1, 311.7));
    return fract(sin(h) * 43758.5453123);
}

float rbox(vec3 p, vec3 s, float r) {
    return length(max(abs(p) - s + vec3(r), 0.0)) - r;
}

vec2 rot(vec2 k, float t) {
    float ct = cos(t);
    float st = sin(t);
    return vec2(ct * k.x - st * k.y, st * k.x + ct * k.y);
}

void oprep2(inout vec2 p, float q, float s, float k) {
    float r = 1.0 / q;
    float angle = atan(p.x, p.y);
    float a = mod(angle, 2.0 * PI * r) - PI * r;
    p.xy = vec2(sin(a), cos(a)) * length(p.xy) - s;
    p.x += s;
}

float map(vec3 p, float t) {
    p.y -= 1.0;
    p.xy = rot(p.xy, p.z * 0.15);
    p.z += t;
    p.xy = mod(p.xy, 6.0) - 0.5 * 6.0;
    p.xy = rot(p.xy, -floor(p.z / 0.75) * 0.35);
    p.z = mod(p.z, 0.75) - 0.5 * 0.75;
    oprep2(p.xy, 6.0, 0.45, t);
    return rbox(p, vec3(0.1, 0.025, 0.25), 0.05);
}

vec3 getNormal(vec3 p, float t) {
    float h = 0.0001;
    return normalize(vec3(
        map(p + vec3(h, 0, 0), t) - map(p - vec3(h, 0, 0), t),
        map(p + vec3(0, h, 0), t) - map(p - vec3(0, h, 0), t),
        map(p + vec3(0, 0, h), t) - map(p - vec3(0, 0, h), t)
    ));
}

float saw(float x, float d, float s, float shift, float t) {
    float xp = PI * (x * d + t * 0.5 + shift);
    float as = asin(s);
    float train = 0.5 * sign(sin(xp - as) - s) + 0.5;
    float range = (PI - 2.0 * as);
    xp = mod(xp, 2.0 * PI);
    float y = mod(-(xp - 2.0 * as), range) / range;
    y *= train;
    return y;
}

vec3 getShading(vec3 p, vec3 normal, vec3 lightPos, float t) {
    vec3 lightDirection = normalize(lightPos - p);
    float lightIntensity = clamp(dot(normal, lightDirection), 0.0, 1.0);
    vec2 id = floor((p.xy + 3.0) / 6.0);
    float fid = hash(id);
    
    float gradientT = fract(t * 0.1 + p.z * 0.1);
    vec3 col = getGradientColor(gradientT);
    
    col *= 4.0 * saw(p.z, 0.092, 0.77, fid * 2.5, t);
    vec3 amb = vec3(0.15, 0.2, 0.32);
    vec3 tex = vec3(0.8098039, 0.8607843, 1.0);
    return col * tex * lightIntensity + amb * (1.0 - lightIntensity);
}

void raymarch(vec3 ro, vec3 rd, float t, out int i, out float dist) {
    dist = 0.0;
    for (int j = 0; j < MAX_STEPS; ++j) {
        vec3 p = ro + rd * dist;
        float h = map(p, t);
        i = j;
        if (h < EPS || dist > FAR) {
            break;
        }
        dist += h * 0.7;
    }
}

float computeSun(vec3 ro, vec3 rd, float dist, float lp) {
    vec3 lpos = vec3(0.0, 0.0, 54.0);
    ro -= lpos;
    float m = dot(rd, -ro);
    float d = length(ro - vec3(0.0, 0.0, 0.7) + m * rd);
    float a = -m;
    float b = dist - m;
    float aa = atan(a / d);
    float ba = atan(b / d);
    float to = (ba - aa) / d;
    return to * 0.15 * lp;
}

vec3 computeColor(vec3 ro, vec3 rd, float t) {
    int i;
    float dist;
    raymarch(ro, rd, t, i, dist);
    float lp = sin(t - 1.0) + 1.3;
    
    float gradientT = fract(t * 0.1);
    vec3 color = getGradientColor(gradientT);
    
    if (i < MAX_STEPS && dist >= NEAR && dist <= FAR) {
        vec3 p = ro + rd * dist;
        vec3 normal = getNormal(p, t);
        float z = 1.0 - (NEAR + dist) / (FAR - NEAR);
        color = getShading(p, normal, vec3(0.0), t);
        color *= lp;
        float zSqrd = z * z;
        color = mix(vec3(0.0), color, zSqrd * (3.0 - 2.0 * z));
        color += computeSun(ro, rd, dist, lp);
        return pow(color, vec3(0.8));
    }
    return color * computeSun(ro, rd, dist, lp);
}

void main() {
    vec2 fragCoord = vUV / CloudScale;
    vec2 coord = fragCoord.xy * 0.84;
    
    float t = Time / CloudSpeed;
    
    vec3 dir = vec3(0.0, 0.0, 1.0);
    vec3 up = vec3(0.0, 1.0, 0.0);
    vec3 right = normalize(cross(dir, up));
    vec3 ro = vec3(0.0, 0.0, 8.74);
    vec3 rd = normalize(dir * 2.0 + coord.x * right + coord.y * up);
    
    vec3 col = computeColor(ro, rd, t);
    
    float alpha = Alpha * clamp(length(col) * 0.8, 0.6, 1.0);
    
    fragColor = vec4(col, alpha);
}

