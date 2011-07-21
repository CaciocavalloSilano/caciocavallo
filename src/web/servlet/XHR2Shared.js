function readShort(array, pos) {
	//TODO: Negative Werte behandeln
	return ((array[pos] << 8) + array[pos + 1]);
}

function readInt(array, pos) {
	//TODO: Negative Werte behandeln
	return ((array[pos] << 24) + (array[pos + 1] << 16) + (array[pos + 2] << 8) + array[pos + 3]);
}

function readXHR2CommandStream() {
	var cmdLength =	readShort(intArray, 0);
	
	var shortBuffer = new Array();
	for(var i=0; i < cmdLength; i++) {
		shortBuffer[i] = readShort(intArray, (i+1)*2);
	}
	
	var result = new Object();
	result.shortBuffer = shortBuffer;
	result.cmdStreamHeight = 0;
	
	return result;
}
