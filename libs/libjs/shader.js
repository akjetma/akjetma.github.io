goog.provide("libjs.shader");

libjs.shader = function () {
  var animate;
  
  function createShaderFromScriptElement(gl, type, id) {
    var shader = gl.createShader(type);
    gl.shaderSource(shader, document.getElementById(id).text);
    gl.compileShader(shader);
    return shader;
  }

  function createProgram(gl, vID, fID) {
    var program = gl.createProgram();
    var vShader = createShaderFromScriptElement(gl, gl.VERTEX_SHADER, vID);
    var fShader = createShaderFromScriptElement(gl, gl.FRAGMENT_SHADER, fID);
    gl.attachShader(program, vShader);
    gl.attachShader(program, fShader);
    gl.linkProgram(program);
    return program;
  }

  function createBuffer(gl, data) {
    var buffer = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
    gl.bufferData(gl.ARRAY_BUFFER, data, gl.STATIC_DRAW);
    return buffer;
  }

  function setTexture(gl, texture, data, size) {
    var rgba = new Uint8Array(size[0] * size[1] * 4);
    for (var i=0; i<data.length; i++) {
      var ii = i * 4;
      rgba[ii] = rgba[ii+1] = rgba[ii+2] = data[i] ? 255 : 0;
      rgba[ii+3] = 255;
    }
    gl.bindTexture(gl.TEXTURE_2D, texture);
    gl.texSubImage2D(gl.TEXTURE_2D, 0, 0, 0, size[0], size[1], gl.RGBA, gl.UNSIGNED_BYTE, rgba);
  }
  
  function createNoise(size) {
    var n = size[0] * size[1];
    var data = new Uint8Array(n);
    for (var i=0; i<n; i++) data[i] = Math.random() < 0.5 ? 0 : 1;
    return data;
  }

  function createTexture(gl, size) {
    var tex = gl.createTexture();        
    gl.bindFramebuffer(gl.FRAMEBUFFER, null);
    gl.bindTexture(gl.TEXTURE_2D, tex);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.REPEAT);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.REPEAT);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST);
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, size[0], size[1], 0, gl.RGBA, gl.UNSIGNED_BYTE, null);
    return tex;
  }

  function draw(gl, viewSize, copy, quad, next) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, null);
    gl.bindTexture(gl.TEXTURE_2D, next);
    gl.viewport(0, 0, viewSize[0], viewSize[1]);
    gl.useProgram(copy);

    var quadAttr = gl.getAttribLocation(copy, 'quad');
    gl.enableVertexAttribArray(quadAttr);
    gl.bindBuffer(gl.ARRAY_BUFFER, quad);
    gl.vertexAttribPointer(quadAttr, 2, gl.FLOAT, false, 0, 0);

    var stateAttr = gl.getUniformLocation(copy, 'state');
    gl.uniform1i(stateAttr, 0);

    var scaleAttr = gl.getUniformLocation(copy, 'scale');
    gl.uniform2fv(scaleAttr, viewSize);
    gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
  }

  function tick(gl, step, stateSize, program, quad, textures) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, step);
    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_2D, textures.prev, 0);
    gl.bindTexture(gl.TEXTURE_2D, textures.next);
    gl.viewport(0, 0, stateSize[0], stateSize[1]);
    gl.useProgram(program);

    var quadAttr = gl.getAttribLocation(program, 'quad');
    gl.enableVertexAttribArray(quadAttr);
    gl.bindBuffer(gl.ARRAY_BUFFER, quad);
    gl.vertexAttribPointer(quadAttr, 2, gl.FLOAT, false, 0, 0);

    var stateAttr = gl.getUniformLocation(program, 'state');
    gl.uniform1i(stateAttr, 0);

    var scaleAttr = gl.getUniformLocation(program, 'scale');
    gl.uniform2fv(scaleAttr, stateSize);
    gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);    

    var tmp = textures.next;
    textures.next = textures.prev;
    textures.prev = tmp;
  }

  function minSquare(size) {
    var pow = 2;
    var square = Math.pow(2, pow);
    while (square - size < 0){
      pow++;
      square = Math.pow(2, pow);
    }
    return square;
  }
  
  function start() {
    var canvas = document.getElementById("shader-canvas");
    var page = document.getElementById ("shader-page");
    canvas.width = page.offsetWidth;
    canvas.height = page.offsetHeight;
    var w = minSquare(canvas.width > canvas.height ? canvas.width : canvas.height);
    var h = w;
    var gl = canvas.getContext("webgl");
    var scale = 2;
    var viewSize = [w, h];
    var stateSize = [w / scale, h / scale];    
    var program = createProgram(gl, "vs", "fs");
    var copy = createProgram(gl, "vs", "copy");
    var quad = createBuffer(gl, new Float32Array([-1, -1, 1, -1, -1, 1, 1, 1]));
    var step = gl.createFramebuffer();
    var noise = createNoise(stateSize);
    var textures = {
      next: createTexture(gl, stateSize),
      prev: createTexture(gl, stateSize)
    }    
    
    setTexture(gl, textures.next, noise, stateSize);
    draw(gl, viewSize, copy, quad, textures.next);
    animate = setInterval(function(){
      tick(gl, step, stateSize, program, quad, textures);
      draw(gl, viewSize, copy, quad, textures.next);
    }, 30);    
  }

  function stop() {
    clearInterval(animate);
    animate = null;
  }
  
  return {
    start: start,
    stop: stop
  }  
}();
