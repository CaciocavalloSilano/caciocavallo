/*
 * Copyright (c) 2011, Clemens Eisserer, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
 
 
/**
 * Backend for receiving binary png-encoded image-data through XHR2.
 * Because there is no way to tell a browser to load an image from
 * binary data, we have to base64 encode the data and set it 
 * using the data-URI scheme, which actually has some overhead.
 */
 
/**
 * Initializes the XHR2Png transport by setting the appropiate
 * function pointers.
 */
function initXHR2Png() {
	initXHR2Shared();
	responseHandlerFunc = handleXHR2PngResponse;
	return "png";
}

/**
 * Determines the offset of the image-data in the binary stream
 * received from the server, and starts base64 encoding it.
 */
function encodeImageData() {
	var cmdLength = readShort(intArray, 0);
	var dataStartPos = 2 * (cmdLength + 1);
	
	if(dataStartPos + 1 < intArray.length) {
		return encode64(intArray, dataStartPos);
	}
	
	return undefined;
}

/**
 * Response handler
 */
function handleXHR2PngResponse() {
	var buffer = xmlhttpreq.response ? xmlhttpreq.response : xmlhttpreq.mozResponseArrayBuffer;
	intArray = new Uint8Array(buffer);
	
	var imgData = encodeImageData();
	if(imgData != undefined) {
		img = new Image();
		img.onload = interpretCommandBuffer;
		img.src = imgData;
	} else {
		interpretCommandBuffer();
	}
}
	 
	 
/**
 * Encodes binary image data to a base64 encoded data-URI.
 * 
 * Parameters:
 * array - the array containing binary data
 * offset - position where to start base64 encoding
 */

 var map = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdef" +
	          "ghijklmnopqrstuvwxyz0123456789+/=";
	          
function encode64(array, offset) {
	//data:image/png;base64,
	var output = new Array('d', 'a', 't', 'a', ':', 'i', 'm', 'a', 'g', 'e', '/', 'p', 'n', 'g', ';', 'b', 'a', 's', 'e', '6', '4', ',');
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
