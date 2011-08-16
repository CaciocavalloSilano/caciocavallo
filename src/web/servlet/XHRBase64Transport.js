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
 * Backend for receiving image-data base64 encoded in text format.
 * Doesn't reliable work on IE9.
 */
 
var parts;

/**
 * Initializes the XHRBase64 transport by setting the appropiate
 * function pointers.
 */
function initXHRBase64() {
	startRequestFunc = StartXHRBase64Request;
	readCmdStreamFunc = readXHRBase64CommandStream;
	responseHandlerFunc = handleXHRBase64Response;
	return "base64";
}

/**
 * Reads the command list values which are received as
 * colon seperated string, which has been split and stored
 * in the global parts-array.
 */
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

/**
 * Receive the command-stream and base64 encoded image
 * data in a colon-speparated string.
 * Split the string, and load the image-data using 
 * the data-URI scheme.
 */
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

/**
 * Starts asynchronous XHR1 request
 */
function StartXHRBase64Request(subSessionID) {
  xmlhttpreq = new XMLHttpRequest();
  xmlhttpreq.open("GET", "ImageStreamer?subsessionid="+subSessionID, true);
  xmlhttpreq.onreadystatechange = xhrSuccessHandler;
  xmlhttpreq.send(null);
}
