
function initXHR1Rle() {
	startRequestFunc = StartXHR1RleRequest;
	readCmdStreamFunc = readBinCommandStream;
	responseHandlerFunc = handleXHR1RLEResponse;
}

function handleXHR1RLEResponse() {
	decodeRLEImageData();
	interpretCommandBuffer();
}


function useMSXHR() {
    return typeof ActiveXObject == "function";
}

function StartXHR1RleRequest(subSessionID) {
    xmlhttpreq = useMSXHR() ? new ActiveXObject("Msxml2.XmlHttp.6.0") : new XMLHttpRequest();
    xmlhttpreq.onreadystatechange = function() {
        if (xmlhttpreq.readyState == 1) {
            if (xmlhttpreq.overrideMimeType) {
                xmlhttpreq.overrideMimeType('text/plain; charset=x-user-defined');
            }
            xmlhttpreq.send();
        }

		var data = new Array();
        if (xmlhttpreq.readyState == 4) {
            if (xmlhttpreq.status == 200) {
				
                if (useMSXHR()) {
                    data = new VBArray(xmlhttpreq.responseBody).toArray();
                    xmlhttpreq.abort();
                } else {
					var txt = xmlhttpreq.responseText;
					for (var i = 0; i < txt.length; i++) {
						data[i] = txt.charCodeAt(i) & 0xff;
					}	
                }
                
               intArray = data;
               responseHandlerFunc();
            } else {
                // Report error
            }
        }
    }
    xmlhttpreq.open("GET", "ImageStreamer?subsessionid="+subSessionID, true);
}

