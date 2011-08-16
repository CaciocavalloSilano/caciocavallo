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
 * Transport for sending the command-stream embedded in an png encoded image.
 * Cmd retrieval is done by using getImageData.
 * Because precision isn't specified, a test is executed checking wether
 * cmd-stream extraction works in the current environment.
 * 
 * Java-Counterpart: ImageTransport.java
 */


var parts;
var cmdCanvas;

/**
 * Sets up the image transport by initializing the required function
 * pointers and returning the server-side transport name.
 */
function initImgTransport() {
	startRequestFunc = StartImageRequest;
	readCmdStreamFunc = readImageCommandStream;
	
	return "img";
}

/**
 * Issues a request to fetch new image-data.
 * Uses a random parameter to avoid caching as far as possible,
 * as most browsers seem to ignore http headers regarding caching
 * when loading images.
 */
function StartImageRequest(subSessionID) {
  var randParam = parseInt(Math.random() * 99999999);
  
  img = new Image();  
  img.src = "ImageStreamer?subsessionid="+subSessionID+"&rand="+randParam;
  img.onload = interpretCommandBuffer;
}


/**
 * Checks wether getImageData delivers the precision
 * required to reconstruct the command-stream from
 * the image.
 * 
 * Precision is not specified, and could be problematic
 * if less than 1 byte per pixel is used to store image data.
 * (which could be the case for 16-bit display depth)
 */
function isImageDataSupported() {
	return false;
	
	//Exclude WebKit for now, as it will trigger a memory leak.
	//TODO: should be version dependent, as its fixed in Chrome 14
	if(navigator.userAgent.indexOf('AppleWebKit') > -1) {
	  return false;	
	}
	
	var canvas = document.createElement('canvas');
	canvas.setAttribute('width', 256);
	canvas.setAttribute('height', 1);
	var ctx = canvas.getContext('2d');
	
	var writeImgData = ctx.createImageData(256, 1);
	var writePixelArray = writeImgData.data; //RGBA
	for(x=0; x < 256; x++) {
		writePixelArray[x*4 + 0] = x;
		writePixelArray[x*4 + 1] = x;
		writePixelArray[x*4 + 2] = x;
		writePixelArray[x*4 + 3] = 255;
	}
	
	ctx.putImageData(writeImgData, 0, 0);
	
	var readImgData = ctx.getImageData(0, 0, 256, 1);
	var readPixelArray = readImgData.data; //RGBA
	
	for(x=0; x < 256; x++) {
		if( (readPixelArray[x*4 + 0] != x) || (readPixelArray[x*4 + 1] != x) || 
		    (readPixelArray[x*4 + 2] != x) || (readPixelArray[x*4 + 3] != 255)) {
		  return false;
		}
	}
	
	return true;
}

/**
 * Reads the amount of rows from the global "img" variable,
 * and returns an ImageData object containing the data.
 * 
 * The temporary canvas is cached when possible, as
 * creating canvas elements is very heavyweight.
 */
function readImgData(height) {
	if(!cmdCanvas || cmdCanvas.getAttribute('width') < img.width || cmdCanvas.getAttribute('height') < height) {
	   cmdCanvas = document.createElement('canvas');
	   cmdCanvas.setAttribute('width', img.width);
	   cmdCanvas.setAttribute('height', height);
    }
	
	var cmdCtx = cmdCanvas.getContext('2d');
	cmdCtx.drawImage(img, 0, 0, img.width, height, 0, 0, img.width, height);
	 
	return cmdCtx.getImageData(0, 0, img.width, height);
}

/**
 * getImageData is used to retrieve the command-stream out of the
 * png-image it is embedded in.
 * Therefor the image is first rendered to a canvas, then the
 * image-data is read back using canvas.getImageData().
 * 
 * The command-stream can also span multiple rows.
 */
function readImageCommandStream() {
	//To know how many rows the cmd-area spans, 
	//we need to read the first line
	var imgData = readImgData(1);
    var imgDataArray = imgData.data;
    var cmdLength = (imgDataArray[0] << 16) + (imgDataArray[1] << 8) + (imgDataArray[2]);
    
    var cmdStreamHeight = Math.ceil((cmdLength+1) / (img.width));
    
    //If the cmd-area spans multiple rows,
    //re-read the cmd-rows.
    if(cmdStreamHeight > 1) {
	   imgData = readImgData(cmdStreamHeight);
	   imgDataArray = imgData.data;
	}
	
	//Decode the ImageData into an array
	//of 16-bit integer values.
	var shortBuffer = new Array();
	for(var i=0; i < cmdLength; i++) {
		var pixelIndex = (i+1)*4;
		var sign = imgDataArray[pixelIndex];         //R
		var highByte = imgDataArray[pixelIndex + 1];  //G 
		var lowByte = imgDataArray[pixelIndex + 2]; //B
		var value = (highByte << 8) + lowByte;
		
		if(sign > 0) {
			value *= -1;
		}		
	
		shortBuffer[i] = value;
	}
	
	//Return the expected result-object
	var result = new Object();
	result.shortBuffer = shortBuffer;
	result.cmdStreamHeight = cmdStreamHeight;
	
	return result;
}
