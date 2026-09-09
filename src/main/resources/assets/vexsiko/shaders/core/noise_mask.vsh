#version 150

layout(std140) uniform NoiseMaskData {
    vec4 rect;      // x, y, ширина, высота — в координатах масштабированного GUI
    vec4 screen;    // xy — размер экрана, z — guiScale, w — порог видимости
    vec4 frame;     // xy — размер фреймбуфера, z — тайлинг шума, w — режим (0 — проявление, 1 — разъедание)
    vec4 tint;      // rgb — цвет затемнения, a — его сила
    vec4 extra;     // x — радиус размытия маски в UV текстуры шума
};

out vec2 noiseCoord;
out vec2 texCoord;
out vec3 dimColor;
out float dimAlpha;
out float reveal;
out float mode;
out float softness;

void main() {
    vec2 positions[6] = vec2[](
        vec2(0.0, 0.0),
        vec2(1.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 0.0),
        vec2(1.0, 1.0),
        vec2(0.0, 1.0)
    );

    vec2 pos = positions[gl_VertexID];
    vec2 screenPos = rect.xy + pos * rect.zw;
    vec2 ndcPos = (screenPos / screen.xy) * 2.0 - 1.0;
    ndcPos.y = -ndcPos.y;

    gl_Position = vec4(ndcPos, 0.0, 1.0);

    // шум тянется на весь экран и тайлится frame.z раз — как сетка квадов в оригинале
    noiseCoord = (screenPos / screen.xy) * frame.z;

    // снимок кадра лежит в координатах фреймбуфера и с перевёрнутым Y
    vec2 fbPos = screenPos * screen.z;
    texCoord = vec2(fbPos.x / frame.x, 1.0 - (fbPos.y / frame.y));

    // ванильное затемнение под экраном — градиент 0xC0101010 → 0xD0101010 по высоте
    dimColor = tint.rgb;
    dimAlpha = mix(0.7529, 0.8157, screenPos.y / screen.y) * tint.a;

    reveal = screen.w;
    mode = frame.w;
    softness = extra.x;
}
