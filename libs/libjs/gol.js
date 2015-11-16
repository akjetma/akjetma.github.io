goog.provide("libjs.gol");

goog.require("libjs.shader");


goog.scope(function () {

  var ls = libjs.shader;



  libjs.gol.put = function(gl, program, quad, size, next) {
    gl.bindTexture(gl.TEXTURE_2D, next);
    gl.viewport(0, 0, size[0], size[1]);
    gl.useProgram(program);
    
    var quadAttr = gl.getAttribLocation(program, 'quad');
    gl.enableVertexAttribArray(quadAttr);
    gl.bindBuffer(gl.ARRAY_BUFFER, quad);
    gl.vertexAttribPointer(quadAttr, 2, gl.FLOAT, false, 0, 0);

    var stateUnif = gl.getUniformLocation(program, 'state');
    gl.uniform1i(stateUnif, 0);

    var scaleUnif = gl.getUniformLocation(program, 'scale');
    gl.uniform2fv(scaleUnif, size);
    gl.drawArrays(gl.TRIANGLE_STRIP, 0, 4);
  }


  
  libjs.gol.draw = function(gl, viewSize, copy, quad, next) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, null);    
    libjs.gol.put(gl, copy, quad, viewSize, next);
  }



  libjs.gol.flip = function(textures) {
    var tmp = textures.next;
    textures.next = textures.prev;
    textures.prev = tmp;
  }


  
  libjs.gol.tick = function(gl, step, stateSize, life, quad, textures) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, step);
    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_2D, textures.prev, 0);
    libjs.gol.put(gl, life, quad, stateSize, textures.next);
    libjs.gol.flip(textures);
  }  



  libjs.gol.prepare = function() {
    var canvas = document.getElementById("shader-canvas");
    var page = document.getElementById ("shader-page");
    canvas.width = page.offsetWidth;
    canvas.height = page.offsetHeight;
    var w = ls.minSquare(canvas.width > canvas.height ? canvas.width : canvas.height);
    var h = w;
    var gl = canvas.getContext("webgl");
    var scale = 2;
    var viewSize = [w, h];
    var stateSize = [w / scale, h / scale];    
    var life = ls.createProgram(gl, "quad", "gol");
    var copy = ls.createProgram(gl, "quad", "copy");
    var quad = ls.createQuadBuffer(gl);
    var step = gl.createFramebuffer();
    var noise = ls.createNoise(stateSize);
    var pixelNoise = ls.binaryPixelData(noise);
    var textures = {
      next: ls.createTexture(gl, stateSize),
      prev: ls.createTexture(gl, stateSize)
    }    
    
    ls.setTexture(gl, textures.next, pixelNoise, stateSize);
    libjs.gol.draw(gl, viewSize, copy, quad, textures.next);

    return function() {
      libjs.gol.tick(gl, step, stateSize, life, quad, textures);
      libjs.gol.draw(gl, viewSize, copy, quad, textures.next);
    };
  }



  libjs.gol.lifecycle = ls.createLifeCycle(libjs.gol.prepare, 30);
  libjs.gol.start = libjs.gol.lifecycle.start;
  libjs.gol.stop = libjs.gol.lifecycle.stop;

});
