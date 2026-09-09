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

out vec2 texCoord;
out vec4 vertexColor;

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
    texCoord = pos;
    vertexColor = vec4(1.0);
    bool isWorld = worldPosSize.w > 0.5 && length(worldPosSize.xyz) > 0.001;
    if (isWorld) {
        float widthPx = max(worldPosSize.w, 4.0);
        float heightPx = max(worldHeight > 0.5 ? worldHeight : widthPx * 1.85, 4.0);
        float width = widthPx / 90.0 * clamp(uSize, 0.5, 2.5);
        float height = heightPx / 90.0 * clamp(uSize, 0.5, 2.5);
        vec3 center = worldPosSize.xyz;
        vec3 camRight = vec3(ModelViewMat[0][0], ModelViewMat[1][0], ModelViewMat[2][0]);
        vec3 camUp    = vec3(ModelViewMat[0][1], ModelViewMat[1][1], ModelViewMat[2][1]);
        vec2 corner = pos * 2.0 - 1.0;
        float yOff = (corner.y + 1.0) * 0.5 * height;
        vec3 offset = camRight * corner.x * width + camUp * yOff;
        vec3 worldPos = center + offset;
        gl_Position = ProjMat * ModelViewMat * vec4(worldPos, 1.0);
    } else {
        float sw = screen.x > 1.0 ? screen.x : 1920.0;
        float sh = screen.y > 1.0 ? screen.y : 1080.0;
        float x = rect.x + pos.x * rect.z;
        float y = rect.y + pos.y * rect.w;
        float ndcX = (x / sw) * 2.0 - 1.0;
        float ndcY = 1.0 - (y / sh) * 2.0;
        gl_Position = vec4(ndcX, ndcY, 0.0, 1.0);
    }
}
