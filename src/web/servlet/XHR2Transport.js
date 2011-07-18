
var xmlhttpreq;
var buffer;

function isXHR2Supported() {
//	xmlhttpreq = new XMLHttpRequest();
	/*if() {
		return true;
	}*/
	
	return false;
}

function readShort(array, pos) {
	//TODO: Negative Werte behandeln
	return ((array[pos*2] << 8) + array[pos*2 + 1]);
}

function readXHR2CommandStream() {
	var intArray = new Uint8Array(buffer);
	var cmdLength =	readShort(intArray, 0);
	
	var shortBuffer = new Array();
	for(var i=0; i < cmdLength; i++) {
		shortBuffer[i] = readShort(intArray, i+1);
	}
	
	var result = new Object();
	result.shortBuffer = shortBuffer;
	result.cmdStreamHeight = 0;
	
	return result;
}

function encodeImageData() {
	var intArray = new Uint8Array(buffer);
	var cmdLength = readShort(intArray, 0);
	
	var dataStartPos = 2 * (cmdLength + 1);
	return encode64(intArray, dataStartPos);
}


function handleXHR2Response() {
  if (xmlhttpreq.readyState==4) {
	  if(xmlhttpreq.status==200) {
		  buffer = xmlhttpreq.response;
		  		
		  img = new Image();
		  img.onload = function() { handleResponse(); }
		  img.src = "data:image/png;base64," + encodeImageData();
	  } else {
		  StartRequest(); 
	  }
  }
}

function StartXHR2Request(subSessionID) {
  xmlhttpreq = new XMLHttpRequest();
  xmlhttpreq.open("GET", "ImageStreamer?subsessionid="+subSessionID, true);
  xmlhttpreq.responseType = 'arraybuffer';

  xmlhttpreq.onreadystatechange = handleXHR2Response;
  xmlhttpreq.send(null);
}


 var map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdef" +
	          "ghijklmnopqrstuvwxyz0123456789+/=";
	 
	  function encode64(array, offset) {

	     var output = new Array();
	     var c1, c2, c3 = "";
	     var i1, i2, i3, i4 = 0;
	 
		for(var i=offset; i < array.length;) { 
			//Divid the input bytes stream into blocks of 3 bytes. 
	        c1 = array[i++];
	        c2 = array[i++];
	        c3 = array[i++];
	 
	        //Divid 24 bits of each 3-byte block into 4 groups of 6 bits. 
	        i1 = c1 >> 2;
	        i2 = ((c1 & 3) << 4) | (c2 >> 4);
	        i3 = ((c2 & 15) << 2) | (c3 >> 6);
	        i4 = c3 & 63;
	 
	        //Pad if our block consists only of 2 or 1 bytes
		    if(c3 == undefined) {
				i4 = 64;
				
				if(c2 == undefined) {
					i3 = 64;
				}
			}
	 
	   
			output[output.length] = map.charAt(i1);
			output[output.length] = map.charAt(i2);
			output[output.length] = map.charAt(i3);
			output[output.length] = map.charAt(i4);
	     }
	 
	     return output.join("");
	  }
