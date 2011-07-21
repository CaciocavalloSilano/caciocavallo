
var xmlhttpreq;
var intArray;

function isXHR2Supported() {	
	return false;
}

function decodeRLEImageData() {
	var cmdLength = readShort(intArray, 0);
	var imgDataStartPos = 2 * (cmdLength + 1);
	
	var w = readShort(intArray, imgDataStartPos);
	var h = readShort(intArray, imgDataStartPos + 2);

	if(!img || img.getAttribute('width') < w || img.getAttribute('height') < h) {
	   img = document.createElement('canvas');
	   img.setAttribute('width', w);
	   img.setAttribute('height', h);
    }

    var ctx = img.getContext('2d');
    var imgData = ctx.createImageData(w, h); //Cache if canvas has *same* size, or rely on dirtyWith parameters?
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

function handleXHR2RLEResponse() {
	if (xmlhttpreq.readyState==4) {
	  if(xmlhttpreq.status==200) {
		  var buffer = xmlhttpreq.response ? xmlhttpreq.response : xmlhttpreq.mozResponseArrayBuffer;
		  intArray = new Uint8Array(buffer);

		  decodeRLEImageData();
		  handleResponse();
	  } else {
		  StartRequest(); 
	  }
  }
}


function encodeImageData() {
	var intArray = new Uint8Array(buffer);
	var cmdLength = readShort(intArray, 0);
	
	var dataStartPos = 2 * (cmdLength + 1);
	return encode64(intArray, dataStartPos);
}


function handleXHR2PngResponse() {
  if (xmlhttpreq.readyState==4) {
	  if(xmlhttpreq.status==200) {
		  var buffer = xmlhttpreq.response ? xmlhttpreq.response : xmlhttpreq.mozResponseArrayBuffer;
		  intArray = new Uint8Array(buffer);
		  		
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

  xmlhttpreq.onreadystatechange = handleXHR2RLEResponse;
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
	 
	        //Pad if block consists only of 2 or 1 bytes
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
