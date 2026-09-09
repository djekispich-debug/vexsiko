#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ProjMat;
uniform mat4 ModuleMat;

out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModuleMat * vec4(Position, 1.0);
    vertexColor = Color;
}
