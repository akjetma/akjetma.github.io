var shaderJS = function () {

  function createShaderFromScriptElement(gl, shader, id) {
    gl.shaderSource(shader, document.getElementById(id).text);
    gl.compileShader(shader);
    return shader;
  }

  function createProgram(gl, program, shaders) {
    for (var i=0; i<shaders.length; i++) gl.attachShader(program, shaders[i]);
    gl.linkProgram(program);
    gl.useProgram(program);    
    return program;
  }

  function initVertexAttribs(gl, positionLocation) {
    gl.enableVertexAttribArray(positionLocation);    
    gl.vertexAttribPointer(positionLocation, 2, gl.FLOAT, false, 0, 0);
  }

  function initialize(canvas, gl, program) {
    gl.uniform2f(
      gl.getUniformLocation(program, "u_resolution"), 
      canvas.width, 
      canvas.height
    );
    gl.bindBuffer(gl.ARRAY_BUFFER, gl.createBuffer());
    gl.bufferData(
      gl.ARRAY_BUFFER, 
      new Float32Array([10, 20, 80, 20, 10, 30, 10, 30, 80, 20, 80, 30]), 
      gl.STATIC_DRAW
    );
    initVertexAttribs(gl, gl.getAttribLocation(program, "a_position"));
    gl.drawArrays(gl.TRIANGLES, 0, 6);
  }

  function initializeGivenCanvasAndGl(canvas, gl) {
    initialize(
      canvas,
      gl,
      createProgram(
        gl, 
        gl.createProgram(), 
        [
          createShaderFromScriptElement(gl, gl.createShader(gl.VERTEX_SHADER), "vs"),
          createShaderFromScriptElement(gl, gl.createShader(gl.FRAGMENT_SHADER), "fs")
        ]
      )
    );
  }

  function initializeGivenCanvas(canvas) {
    initializeGivenCanvasAndGl(canvas, canvas.getContext("webgl"));
  }

  function setupCanvas(canvas, page) {
    canvas.width = page.offsetWidth;
    canvas.height = page.offsetHeight;
    return canvas;
  }

  function start() {
    initializeGivenCanvas(setupCanvas(
      document.getElementById("shader-canvas"),
      document.getElementById("shader-page")
    )); 
  }
 
  function stop() {}
  
  return {
    start: start,
    stop: stop
  }
}();
