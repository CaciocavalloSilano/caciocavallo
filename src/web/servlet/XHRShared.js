var xmlhttpreq;
var responseHandlerFunc;
var intArray;

function readShort(array, pos) {
	var highByte = array[pos];
	var lowByte = array[pos + 1];
	
	var value = ((highByte & 127) << 8) + lowByte;
	if((highByte & 128) != 0) {
		value *= -1;
	}
	
	return value;
}

function readInt(array, pos) {	
	return ((array[pos] << 24) + (array[pos + 1] << 16) + (array[pos + 2] << 8) + array[pos + 3]);
}

function readBinCommandStream() {
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

function xhrSuccessHandler() {
	if (xmlhttpreq.readyState==4) {
	  if(xmlhttpreq.status==200) {
		  responseHandlerFunc();
	  } else {
		  //TODO: Alert
		  //startRequestFunc(subSessionID);
	  }
  }
}
