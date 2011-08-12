var parts;

function initXHRBase64() {
	startRequestFunc = StartXHRBase64Request;
	readCmdStreamFunc = readXHRBase64CommandStream;
	responseHandlerFunc = handleXHRBase64Response;
	return "base64";
}

function readXHRBase64CommandStream() {
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

function handleXHRBase64Response() {
	parts = xmlhttpreq.responseText.split(":");
	
	var base64ImageData = parts[parts.length-1];
	if(parts.length >= 2 && base64ImageData.length > 0) {
		img = new Image();
		img.onload = interpretCommandBuffer;
		img.src = "data:image/png;base64," + base64ImageData;
	}else {
		interpretCommandBuffer();
	}
}

function StartXHRBase64Request(subSessionID) {
  xmlhttpreq = new XMLHttpRequest();
  xmlhttpreq.open("GET", "ImageStreamer?subsessionid="+subSessionID, true);
  xmlhttpreq.onreadystatechange = xhrSuccessHandler;
  xmlhttpreq.send(null);
}
