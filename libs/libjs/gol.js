goog.provide("libjs.gol");

goog.require("libjs.shader");

libjs.gol = function () {
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
  
  function start() {
    var canvas = document.getElementById("shader-canvas");
    var page = document.getElementById ("shader-page");
    canvas.width = page.offsetWidth;
    canvas.height = page.offsetHeight;
    var w = libjs.shader.minSquare(canvas.width > canvas.height ? canvas.width : canvas.height);
    var h = w;
    var gl = canvas.getContext("webgl");
    var scale = 2;
    var viewSize = [w, h];
    var stateSize = [w / scale, h / scale];    
    var gol = libjs.shader.createProgram(gl, "quad", "gol");
    var copy = libjs.shader.createProgram(gl, "quad", "copy");
    var quad = libjs.shader.createQuadBuffer(gl);
    var step = gl.createFramebuffer();
    var noise = libjs.shader.createNoise(stateSize);
    var pixelNoise = libjs.shader.binaryPixelData(noise);
    var textures = {
      next: libjs.shader.createTexture(gl, stateSize),
      prev: libjs.shader.createTexture(gl, stateSize)
    }    
    
    libjs.shader.setTexture(gl, textures.next, pixelNoise, stateSize);
    draw(gl, viewSize, copy, quad, textures.next);

    return function() {
      tick(gl, step, stateSize, gol, quad, textures);
      draw(gl, viewSize, copy, quad, textures.next);
    };
  }

  return libjs.shader.createLifeCycle(start, 30);
}();
