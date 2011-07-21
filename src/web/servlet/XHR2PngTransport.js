
var xmlhttpreq;
var intArray;

function isXHR2Supported() {	
	return false;
}


function encodeImageData() {
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
		  img.onload = interpretCommandBuffer;
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

        //data:image/png;base64,
		var output = new Array('d', 'a', 't', 'a', ':', 'i', 'm', 'a', 'g', 'e', '/', 'p', 'n', 'g', ';', 'b', 'a', 's', 'e', '6', '4');
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
