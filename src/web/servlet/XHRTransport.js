function readXHRCommandStream() {
	var cmdLength = parseInt(parts[0]);
	var shortBuffer = new Array(cmdLength);
	for(var i=0; i < cmdLength; i++) {
		shortBuffer[i] = parseInt(parts[i+1]);
	}
	
	var result = new Object();
	result.shortBuffer = shortBuffer;
	result.cmdStreamHeight = 0;
	
	return result;
}

var xmlhttpreq;

function handleXHRResponse() {
  if (xmlhttpreq.readyState==4) {
	  if(xmlhttpreq.status==200) {
		  parts = xmlhttpreq.responseText.split(':');
		  				
		  img = new Image();
		  img.onload = function() { handleResponse(); }
		  img.src = "data:image/png;base64," + parts[parts.length-1]; ;	
	  } else {
		  StartRequest(); 
	  }
  }
}

function StartXHRRequest(subSessionID) {
  xmlhttpreq = new XMLHttpRequest();
  xmlhttpreq.open("GET", "ImageStreamer?subsessionid="+subSessionID, true);
  xmlhttpreq.onreadystatechange = handleXHRResponse;
  xmlhttpreq.send(null);
}
