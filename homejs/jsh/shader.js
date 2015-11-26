goog.provide('jsh.shader');

jsh.shader.createShader = function(gl, type, id) {
  var shader = gl.createShader(type);
  gl.shaderSource(shader, document.getElementById(id).text);
  gl.compileShader(shader);
  return shader;
};

jsh.shader.createProgram = function(gl, vID, fID) {
  var program = gl.createProgram();
  var vShader = jsh.shader.createShader(gl, gl.VERTEX_SHADER, vID);
  var fShader = jsh.shader.createShader(gl, gl.FRAGMENT_SHADER, fID);
  gl.attachShader(program, vShader);
  gl.attachShader(program, fShader);
  gl.linkProgram(program);
  return program;
};

jsh.shader.setWrapScale = function(gl, wrap, scale) {
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, wrap);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, wrap);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, scale);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, scale);
};

jsh.shader.texSubImage2D = function(gl, data, size) {
  gl.texSubImage2D(gl.TEXTURE_2D, 0, 0, 0, size[0], size[1], gl.RGBA, gl.UNSIGNED_BYTE, data);
};

jsh.shader.texImage2D = function(gl, data, size) {  
  if (arguments.length == 2) {
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, data);
  } else {
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, size[0], size[1], 0, gl.RGBA, gl.UNSIGNED_BYTE, data);
  }
};

jsh.shader.createVideoTexture = function(gl) {
  var tex = gl.createTexture();        
  gl.bindTexture(gl.TEXTURE_2D, tex);
  gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
  jsh.shader.setWrapScale(gl, gl.CLAMP_TO_EDGE, gl.NEAREST);
  jsh.shader.texImage2D(gl, null);
  gl.bindTexture(gl.TEXTURE_2D, null);
  return tex;
};

jsh.shader.createTexture = function(gl, size) {
  var tex = gl.createTexture();        
  gl.bindTexture(gl.TEXTURE_2D, tex);
  jsh.shader.setWrapScale(gl, gl.REPEAT, gl.NEAREST);
  jsh.shader.texImage2D(gl, null, size);
  gl.bindTexture(gl.TEXTURE_2D, null);
  return tex;
};

jsh.shader.createQuadBuffer = function(gl) {
  var buffer = gl.createBuffer();
  gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
  gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([-1, -1, 1, -1, -1, 1, 1, 1]), gl.STATIC_DRAW);
  gl.bindBuffer(gl.ARRAY_BUFFER, null);
  return buffer;
};

jsh.shader.minSquare = function(size) {
  var pow = 2;
  var square = Math.pow(2, pow);
  while (square - size < 0) {
    pow++;
    square = Math.pow(2, pow);
  }
  return square;
};

jsh.shader.createNoise = function(size, density) {
  var n = size[0] * size[1];
  var data = new Uint8Array(n);
  for (var i=0; i<n; i++) data[i] = Math.random() < density ? 1 : 0;
  return data;
};

jsh.shader.binaryPixelData = function(states) {
  var rgba = new Uint8Array(states.length * 4);
  for (var i=0; i<states.length; i++) {
    var j = i * 4;
    rgba[j] = rgba[j+1] = rgba[j+2] = states[i] * 255;
    rgba[j+3] = 255;
  }
  return rgba;
};

jsh.shader.createLifeCycle = function(prepare, rate) {
  var animate = null;
  
  return {
    start: function() {
      var tick = prepare();
      animate = setInterval(tick, rate);
    },
    stop: function() {
      clearInterval(animate);
      animate = null;
    }
  };
};
