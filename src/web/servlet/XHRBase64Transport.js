function initXHR2Rle() {
	startRequestFunc = StartXHRRequest;
	readCmdStreamFunc = readXHRCommandStream;
	responseHandlerFunc = handleXHRResponse;
}

function readXHRCommandStream() {
	var cmdLength = parseInt(parts[0]);
	
	var shortBuffer = new Array();
	for(var i=0; i < cmdLength; i++) {
		shortBuffer[i] = parseInt(parts[i+1]);
	}
	
	var result = new Object();
	result.shortBuffer = shortBuffer;
	result.cmdStreamHeight = 0;
	
	return result;
}

function handleXHRResponse() {
	img = new Image();
	img.onload = interpretCommandBuffer;
	img.src = "data:image/png;base64," + parts[parts.length-1];
}

function StartXHRRequest(subSessionID) {
  xmlhttpreq = new XMLHttpRequest();
  xmlhttpreq.open("GET", "ImageStreamer?subsessionid="+subSessionID, true);
  xmlhttpreq.onreadystatechange = handleXHRResponse;
  xmlhttpreq.send(null);
}
