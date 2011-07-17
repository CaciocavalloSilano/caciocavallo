var parts;

function StartImageRequest(subSessionID) {
  var randParam = parseInt(Math.random() * 99999999);
  
  img = new Image();  
  img.src = "ImageStreamer?subsessionid="+subSessionID+"&rand="+randParam;
  img.onload = handleResponse;
}

function isImageDataSupported() {
	var canvas = document.createElement('canvas');
	canvas.setAttribute('width', 256);
	canvas.setAttribute('height', 1);
	var ctx = canvas.getContext('2d');
	
	var writeImgData = ctx.createImageData(256, 1);
	var writePixelArray = writeImgData.data; //RGBA
	for(x=0; x < 256; x++) {
		writePixelArray[x*4 + 0] = x;
		writePixelArray[x*4 + 1] = x;
		writePixelArray[x*4 + 2] = x;
		writePixelArray[x*4 + 3] = 255;
	}
	
	ctx.putImageData(writeImgData, 0, 0);
	
	var readImgData = ctx.getImageData(0, 0, 256, 1);
	var readPixelArray = readImgData.data; //RGBA
	
	for(x=0; x < 256; x++) {
		if( (readPixelArray[x*4 + 0] != x) || (readPixelArray[x*4 + 1] != x) || 
		    (readPixelArray[x*4 + 2] != x) || (readPixelArray[x*4 + 3] != 255)) {
		  return false;
		}
	}
	
	return true;
}

function readImgData(height) {
	var cmdCanvas = document.createElement('canvas');
	cmdCanvas.setAttribute('width', img.width);
	cmdCanvas.setAttribute('height', height);
	
	var cmdCtx = cmdCanvas.getContext('2d');
	cmdCtx.drawImage(img, 0, 0);
	 
	return cmdCtx.getImageData(0, 0, cmdCanvas.width, height);
}

function readImageCommandStream() {
	var imgData = readImgData(1); //Optimize to be higher by default for small images
    var imgDataArray = imgData.data;
    var cmdLength = (imgDataArray[0] << 16) + (imgDataArray[1] << 8) + (imgDataArray[2]);
    
    var cmdStreamHeight = Math.ceil((cmdLength+1) / (img.width));
    
    if(cmdStreamHeight > 1) {
	   imgData = readImgData(cmdStreamHeight);
	   imgDataArray = imgData.data;
	}
	
	var shortBuffer = new Array(cmdLength);
	for(var i=0; i < cmdLength; i++) {
		var pixelIndex = (i+1)*4;
		var sign = imgDataArray[pixelIndex];         //R
		var highByte = imgDataArray[pixelIndex + 1];  //G 
		var lowByte = imgDataArray[pixelIndex + 2]; //B
		var value = (highByte << 8) + lowByte;
		
		if(sign == 1) {
			value *= -1;
		}		
	
		shortBuffer[i] = value;
	}
	
	var result = new Object();
	result.shortBuffer = shortBuffer;
	result.cmdStreamHeight = cmdStreamHeight;
	
	return result;
}
