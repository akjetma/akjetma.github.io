goog.provide("libjs.face");

goog.require("libjs.shader");
goog.require("home.util");

goog.scope(function () {
  var ls = libjs.shader;
  
  libjs.face.put = function(gl, program, quad, size, state) {
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



  libjs.face.flip = function(textures) {
    var tmp = textures.next;
    textures.next = textures.prev;
    textures.prev = tmp;
  }


  
  libjs.face.tick = function(gl, step, stateSize, life, quad, textures, video, videoSize) {
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
    libjs.face.put(gl, life, quad, stateSize, textures.next);
    libjs.face.flip(textures);
  }  
  


  libjs.face.draw = function(gl, viewSize, copy, quad, next) {
    gl.bindFramebuffer(gl.FRAMEBUFFER, null);
    libjs.face.put(gl, copy, quad, viewSize, next);
  }
  
  
  
  libjs.face.animate = null;



  libjs.face.stop = function() {
    clearInterval(libjs.face.animate);
    libjs.face.animate = null;
  }



  libjs.face.start = function() {
    home.util.setgum();        
    
    navigator.getUserMedia({video: true}, function(stream) {      
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
          next: ls.createTex(gl, stateSize),
          prev: ls.createTex(gl, stateSize),
          video: ls.videoTex(gl)
        }
        gl.activeTexture(gl.TEXTURE0);
        gl.bindTexture(gl.TEXTURE_2D, textures.next);
        ls.texSubImage2D(gl, pixelNoise, stateSize);
        libjs.face.draw(gl, viewSize, copy, quad, textures.next);
        libjs.face.animate = setInterval(function() {
          libjs.face.tick(gl, step, stateSize, life, quad, textures, video, videoSize);
          libjs.face.draw(gl, viewSize, copy, quad, textures.next);
        }, 30);
      });
      
      
            
    }, function(error) {
      console.log("something happened");
    });        
  }
  
  

});
