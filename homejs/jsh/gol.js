goog.provide("jsh.gol");

goog.require("jsh.shader");


goog.scope(function () {

  var ls = jsh.shader;



  jsh.gol.put = function(gl, program, quad, size, next) {
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


  
  jsh.gol.draw = function(gl, viewSize, copy, quad, next) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, null);    
    jsh.gol.put(gl, copy, quad, viewSize, next);
  }



  jsh.gol.flip = function(textures) {
    var tmp = textures.next;
    textures.next = textures.prev;
    textures.prev = tmp;
  }


  
  jsh.gol.tick = function(gl, step, stateSize, life, quad, textures) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, step);
    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_2D, textures.prev, 0);
    jsh.gol.put(gl, life, quad, stateSize, textures.next);
    jsh.gol.flip(textures);
  }  



  jsh.gol.prepare = function() {
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
    var noise = ls.createNoise(stateSize, 0.2);
    var pixelNoise = ls.binaryPixelData(noise);
    var textures = {
      next: ls.createTexture(gl, stateSize),
      prev: ls.createTexture(gl, stateSize)
    }    

    gl.bindTexture(gl.TEXTURE_2D, textures.next);
    ls.texSubImage2D(gl, pixelNoise, stateSize);
    jsh.gol.draw(gl, viewSize, copy, quad, textures.next);

    return function() {
      jsh.gol.tick(gl, step, stateSize, life, quad, textures);
      jsh.gol.draw(gl, viewSize, copy, quad, textures.next);
    };
  }



  jsh.gol.lifecycle = ls.createLifeCycle(jsh.gol.prepare, 30);
  jsh.gol.start = jsh.gol.lifecycle.start;
  jsh.gol.stop = jsh.gol.lifecycle.stop;

});
