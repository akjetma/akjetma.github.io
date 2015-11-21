goog.provide("libjs.shader");



libjs.shader.createShader = function(gl, type, id) {
  var shader = gl.createShader(type);
  gl.shaderSource(shader, document.getElementById(id).text);
  gl.compileShader(shader);
  return shader;
}



libjs.shader.createProgram = function(gl, vID, fID) {
  var program = gl.createProgram();
  var vShader = libjs.shader.createShader(gl, gl.VERTEX_SHADER, vID);
  var fShader = libjs.shader.createShader(gl, gl.FRAGMENT_SHADER, fID);
  gl.attachShader(program, vShader);
  gl.attachShader(program, fShader);
  gl.linkProgram(program);
  return program;
}



libjs.shader.minSquare = function(size) {
  var pow = 2;
  var square = Math.pow(2, pow);
  while (square - size < 0){
    pow++;
    square = Math.pow(2, pow);
  }
  return square;
}



libjs.shader.createBuffer = function(gl, data) {
  var buffer = gl.createBuffer();
  gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
  gl.bufferData(gl.ARRAY_BUFFER, data, gl.STATIC_DRAW);
  return buffer;
}



libjs.shader.createNoise = function(size, density) {
  var n = size[0] * size[1];
  var data = new Uint8Array(n);
  for (var i=0; i<n; i++) data[i] = Math.random() < density ? 1 : 0;
  return data;
}



libjs.shader.createTexture = function(gl) {
  var tex = gl.createTexture();        
  gl.bindFramebuffer(gl.FRAMEBUFFER, null);
  gl.bindTexture(gl.TEXTURE_2D, tex);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.REPEAT);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.REPEAT);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST);
  return tex;
}



libjs.shader.videoTex = function(gl) {
  var tex = gl.createTexture();        
  gl.bindFramebuffer(gl.FRAMEBUFFER, null);
  gl.bindTexture(gl.TEXTURE_2D, tex);
  gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST);
  gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST);
  libjs.shader.texImage2D(gl, null);
  return tex;
}



libjs.shader.createTex = function(gl, size) {
  var tex = libjs.shader.createTexture(gl);
  if (arguments.length == 1) {
    libjs.shader.texImage2D(gl, null);
  } else {
    libjs.shader.texImage2D(gl, null, size);
  }
  return tex;
}



libjs.shader.createQuadBuffer = function(gl) {
  return libjs.shader.createBuffer(gl, new Float32Array([-1, -1, 1, -1, -1, 1, 1, 1]));
}



libjs.shader.binaryPixelData = function(states) {
  var rgba = new Uint8Array(states.length * 4);
  for (var i=0; i<states.length; i++) {
    var j = i * 4;
    rgba[j] = rgba[j+1] = rgba[j+2] = states[i] * 255;
    rgba[j+3] = 255;
  }
  return rgba;
}



libjs.shader.texSubImage2D = function(gl, data, size) {
  gl.texSubImage2D(gl.TEXTURE_2D, 0, 0, 0, size[0], size[1], gl.RGBA, gl.UNSIGNED_BYTE, data);
}



libjs.shader.texImage2D = function(gl, data, size) {
  if (arguments.length == 2) {
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, data);
  } else {
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, size[0], size[1], 0, gl.RGBA, gl.UNSIGNED_BYTE, data);
  }
}



libjs.shader.createLifeCycle = function(prepare, rate) {
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
}
