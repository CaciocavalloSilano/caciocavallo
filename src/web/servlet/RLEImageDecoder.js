function decodeRLEImageData() {
	var cmdLength = readShort(intArray, 0);
	var imgDataStartPos = 2 * (cmdLength + 1);
	
	if(intArray.length <= (imgDataStartPos + 1)) {
		return;
	}
	
	var w = readShort(intArray, imgDataStartPos);
	var h = readShort(intArray, imgDataStartPos + 2);

	if(!img || img.getAttribute('width') < w || img.getAttribute('height') < h) {
	   img = document.createElement('canvas');
	   img.setAttribute('width', w);
	   img.setAttribute('height', h);
    }

    var ctx = img.getContext('2d');
    var imgData = ctx.createImageData(w, h); //Cache if canvas has *same* size, or even rely on dirtyWith parameters?
	var imgDataArray = imgData.data;
   
    var runDataLength = readInt(intArray, imgDataStartPos + 4);
   	var runLengthDataOffset = imgDataStartPos + 8;
	var pixelDataOffset = runLengthDataOffset + runDataLength;
 
	var imgDataOffset = 0;
	var lastRed = 0, lastGreen = 0, lastBlue = 0;
    for(var i= 0; i < runDataLength; i++) {
		var cmd = intArray[runLengthDataOffset + i];
		var length = cmd & 127;
		
		if(cmd < 128) {
			for (var x = 0; x < length; x++) {
				imgDataArray[imgDataOffset++] = lastRed;
				imgDataArray[imgDataOffset++] = lastGreen;
				imgDataArray[imgDataOffset++] = lastBlue;
				imgDataArray[imgDataOffset++] = 255;
			}
		}else {
			for (var x = 0; x < length; x++) {		
				imgDataArray[imgDataOffset++] = lastRed =  intArray[pixelDataOffset++];
				imgDataArray[imgDataOffset++] = lastGreen = intArray[pixelDataOffset++];
				imgDataArray[imgDataOffset++] = lastBlue = intArray[pixelDataOffset++];
				imgDataArray[imgDataOffset++] = 255;
			}
		}
	}
	
	ctx.putImageData(imgData, 0, 0);
}
