#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D state;
uniform vec2 scale;

int get(vec2 offset) {
  return int(texture2D(state, (gl_FragCoord.xy + offset) / scale).r);
}

int getSum() {
  int sum = -1 * get(vec2(0,0));

  for (int i=-1; i<=1; i++) {
    for (int j=-1; j<=1; j++) {
      sum += get(vec2(i,j));
    }
  }

  return sum;
}

void main() {
  int sum = getSum();
  
  if (sum == 3) {
    gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
  } else if (sum == 2) {
    float current = float(get(vec2(0.0, 0.0)));
    gl_FragColor = vec4(current, current, current, 1.0);
  } else {
    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
  }
}
