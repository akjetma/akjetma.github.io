#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D state;
uniform sampler2D video;
uniform vec2 scale;
uniform vec2 videoScale;

vec4 get(sampler2D texture, vec2 coord) {
  return texture2D(texture, coord);
}

int getState(vec2 offset) {
  return int(get(state, (gl_FragCoord.xy + offset) / scale).r);
}

vec4 getVideo(vec2 offset) {
  return get(video, (gl_FragCoord.xy + offset) / videoScale);
}

int getSum() {
  int sum = -1 * getState(vec2(0,0));

  for (int i=-1; i<=1; i++) {
    for (int j=-1; j<=1; j++) {
      sum += getState(vec2(i,j));
    }
  }

  return sum;
}

float intensity() {
  vec4 p = getVideo(vec2(0.0,0.0));
  return (p.r + p.g + p.b);
}

float detectEdge() {
  float diff = 0.0;
  vec4 corners[4];
  corners[0] = getVideo(vec2(-1.0, -1.0));
  corners[1] = getVideo(vec2(-1.0, 1.0));
  corners[2] = getVideo(vec2(1.0, -1.0));
  corners[3] = getVideo(vec2(1.0, 1.0));

  for (int chan=0; chan<=2; chan++) {
    diff += abs(corners[0][chan] - corners[3][chan]);
    diff += abs(corners[1][chan] - corners[2][chan]);
  }

  return diff;
}

void main() {
  int sum = getSum();
  float edge = detectEdge();
  float light = intensity();
  
  if (sum == 3 || edge >= 0.7 || light >= 1.7) {
    gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
  } else if (sum == 2) {
    float current = float(getState(vec2(0.0, 0.0)));
    gl_FragColor = vec4(current, current, current, 1.0);  
  } else {
    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
  }
}
