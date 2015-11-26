#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_photo0;
uniform vec2 u_res;

void main() {
  gl_FragColor = texture2D(u_photo0, gl_FragCoord.xy / u_res);
}
