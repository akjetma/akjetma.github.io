attribute vec2 a_position;
uniform vec2 u_resolution;

void main() {
	gl_Position = vec4(vec2(1, -1) * (((a_position / u_resolution) * 2.0) - 1.0), 0, 1);
}
