var shaderJS = function () {
  var animate;
  
  function start () {
    var bar = document.getElementById("bar");
    var gl = twgl.getWebGLContext(document.getElementById("shader-canvas"));
    var programInfo = twgl.createProgramInfo(gl, ["vs", "fs"]);    
    var arrays = {
      position: [-1, -1, 0, 1, -1, 0, -1, 1, 0, -1, 1, 0, 1, -1, 0, 1, 1, 0],
    };
    var bufferInfo = twgl.createBufferInfoFromArrays(gl, arrays);
    var mouseX, mouseY, clientXLast, clientYLast = 0;
    
    document.addEventListener("mousemove", function (e) {
      var clientX = e.clientX;
      var clientY = e.clientY;

      if (clientXLast == clientX && clientYLast == clientY) {
        return;
      }

      clientXLast = clientX;
      clientYLast = clientY;

      mouseX = (clientX - bar.offsetWidth) / window.innerWidth;
      mouseY = 1 - clientY / window.innerHeight;
    });
    
    function render(time) {      
      var uniforms = {
        time: time * 0.001,
        resolution: [gl.canvas.width, gl.canvas.height],
        mouse: [mouseX, mouseY]
      };
      
      gl.useProgram(programInfo.program);
      twgl.setBuffersAndAttributes(gl, programInfo, bufferInfo);
      twgl.setUniforms(programInfo, uniforms);
      twgl.drawBufferInfo(gl, gl.TRIANGLES, bufferInfo);
      
      animate = requestAnimationFrame(render);
    }
    
    animate = requestAnimationFrame(render);
  }

  function stop () {
    cancelAnimationFrame(animate);
  }
 
  return {
    start: start,
    stop: stop
  }
}();
