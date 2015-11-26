goog.provide('jsh.fol');
goog.require('jsh.shader');
goog.require('home.util');

goog.scope(function () {
  var ls = jsh.shader;
  
  jsh.fol.put = function(gl, program, quad, size, state) {
    gl.activeTexture(gl.TEXTURE0);
    gl.bindTexture(gl.TEXTURE_2D, state);
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
  };

  jsh.fol.flip = function(textures) {
    var tmp = textures.next;
    textures.next = textures.prev;
    textures.prev = tmp;
  };
  
  jsh.fol.tick = function(gl, step, stateSize, life, quad, textures, video, videoSize) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, step);
    gl.activeTexture(gl.TEXTURE0);
    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_2D, textures.prev, 0);
    gl.useProgram(life);
    gl.activeTexture(gl.TEXTURE1);
    gl.bindTexture(gl.TEXTURE_2D, textures.video);
    ls.texImage2D(gl, video);
    
    var videoUnif = gl.getUniformLocation(life, 'video');
    gl.uniform1i(videoUnif, 1);

    var videoScaleUnif = gl.getUniformLocation(life, 'videoScale');
    gl.uniform2fv(videoScaleUnif, videoSize);
    jsh.fol.put(gl, life, quad, stateSize, textures.next);
    jsh.fol.flip(textures);
  }; 
  
  jsh.fol.draw = function(gl, viewSize, copy, quad, next) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, null);
    jsh.fol.put(gl, copy, quad, viewSize, next);
  };
  
  jsh.fol.animate = null;

  jsh.fol.stop = function() {
    clearInterval(jsh.fol.animate);
    jsh.fol.animate = null;
  };

  jsh.fol.gotStream = function(stream) {      
    var video = document.createElement("video");
    video.autoplay = true;
    video.src = URL.createObjectURL(stream);
    video.addEventListener("canplay", function () {
      var page = document.getElementById("fol-page");
      var output = document.getElementById("fol-canvas");
      output.width = page.offsetWidth;
      output.height = page.offsetHeight;
      var w = ls.minSquare(output.width > output.height ? output.width : output.height);
      var h = w;
      video.width = w;
      video.height = h;
      var gl = output.getContext("webgl");
      var scale = 2;
      var viewSize = [w, h];
      var stateSize = [w / scale, h / scale];
      var videoSize = [video.videoWidth, video.videoHeight];
      var life = ls.createProgram(gl, "quad", "fol");
      var copy = ls.createProgram(gl, "quad", "copy");
      var quad = ls.createQuadBuffer(gl);
      var step = gl.createFramebuffer();
      var noise = ls.createNoise(stateSize, 0.05);
      var pixelNoise = ls.binaryPixelData(noise);
      var textures = {
        next: ls.createTexture(gl, stateSize),
        prev: ls.createTexture(gl, stateSize),
        video: ls.createVideoTexture(gl)
      };
      gl.activeTexture(gl.TEXTURE0);
      gl.bindTexture(gl.TEXTURE_2D, textures.next);
      ls.texSubImage2D(gl, pixelNoise, stateSize);
      jsh.fol.draw(gl, viewSize, copy, quad, textures.next);
      jsh.fol.animate = setInterval(function() {
        jsh.fol.tick(gl, step, stateSize, life, quad, textures, video, videoSize);
        jsh.fol.draw(gl, viewSize, copy, quad, textures.next);
      }, 30);
    });    
  };

  jsh.fol.didntGetStream = function(error) {
    console.log("didn't get the stream", error);
  };
  
  jsh.fol.start = function() {
    home.util.setgum();        
    
    navigator.getUserMedia({video: true}, jsh.fol.gotStream, jsh.fol.didntGetStream);        
  };
  
});
