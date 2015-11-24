goog.provide("jsh.face");

goog.require("jsh.shader");
goog.require("home.util");

goog.scope(function () {
  var ls = jsh.shader;
  
  jsh.face.put = function(gl, program, quad, size, state) {
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
  }

  jsh.face.flip = function(textures) {
    var tmp = textures.next;
    textures.next = textures.prev;
    textures.prev = tmp;
  }
  
  jsh.face.tick = function(gl, step, stateSize, life, quad, textures, video, videoSize) {
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
    jsh.face.put(gl, life, quad, stateSize, textures.next);
    jsh.face.flip(textures);
  }  
  
  jsh.face.draw = function(gl, viewSize, copy, quad, next) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, null);
    jsh.face.put(gl, copy, quad, viewSize, next);
  }
  
  jsh.face.animate = null;

  jsh.face.stop = function() {
    clearInterval(jsh.face.animate);
    jsh.face.animate = null;
  }

  jsh.face.gotStream = function(stream) {      
    var video = document.createElement("video");
    video.autoplay = true;
    video.src = URL.createObjectURL(stream);
    video.addEventListener("canplay", function () {
      var page = document.getElementById("shader-2-page");
      var output = document.getElementById("shader-2-canvas");
      output.width = page.offsetWidth;
      output.height = page.offsetHeight;
      var w = ls.minSquare(output.width > output.height ? output.width : output.height);
      var h = w;
      video.width = w;
      video.height = h;
      var gl = output.getContext("webgl");
      var scale = 4;
      var viewSize = [w, h];
      var stateSize = [w / scale, h / scale];
      var videoSize = [video.videoWidth / 2, video.videoHeight / 2];
      var life = ls.createProgram(gl, "quad", "face");
      var copy = ls.createProgram(gl, "quad", "copy");
      var quad = ls.createQuadBuffer(gl);
      var step = gl.createFramebuffer();
      var noise = ls.createNoise(stateSize, 0.50);
      var pixelNoise = ls.binaryPixelData(noise);
      var textures = {
        next: ls.createTexture(gl, stateSize),
        prev: ls.createTexture(gl, stateSize),
        video: ls.createVideoTexture(gl)
      }
      gl.activeTexture(gl.TEXTURE0);
      gl.bindTexture(gl.TEXTURE_2D, textures.next);
      ls.texSubImage2D(gl, pixelNoise, stateSize);
      jsh.face.draw(gl, viewSize, copy, quad, textures.next);
      jsh.face.animate = setInterval(function() {
        jsh.face.tick(gl, step, stateSize, life, quad, textures, video, videoSize);
        jsh.face.draw(gl, viewSize, copy, quad, textures.next);
      }, 30);
    });    
  }

  jsh.face.didntGetStream = function(error) {
    console.log("didn't get the stream", error);
  }
  
  jsh.face.start = function() {
    home.util.setgum();        
    
    navigator.getUserMedia({video: true}, jsh.face.gotStream, jsh.face.didntGetStream);        
  }
  
});
