#version 300 es

uniform mat4 uMVPMatrix, worldMat;
uniform vec3 eyePos;

layout(location = 5) in vec4 vPosition;
layout(location = 6) in vec4 vColor;
layout(location = 7) in vec3 vNormal;

out vec4 fColor;
out vec3 fNormal, fView;

void main() {
    gl_Position = uMVPMatrix * vPosition;
    fColor = vColor;
    fNormal = normalize(transpose(inverse(mat3(worldMat))) * vNormal);
    fView = normalize(eyePos - (worldMat * vPosition).xyz);
}