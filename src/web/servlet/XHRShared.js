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
 
var xmlhttpreq;
var responseHandlerFunc;
var intArray;

function readShort(array, pos) {
	var highByte = array[pos];
	var lowByte = array[pos + 1];
	
	var value = ((highByte & 127) << 8) + lowByte;
	if((highByte & 128) != 0) {
		value *= -1;
	}
	
	return value;
}

function readInt(array, pos) {	
	return ((array[pos] << 24) + (array[pos + 1] << 16) + (array[pos + 2] << 8) + array[pos + 3]);
}

function readBinCommandStream() {
	var cmdLength =	readShort(intArray, 0);
	
	var shortBuffer = new Array();
	for(var i=0; i < cmdLength; i++) {
		shortBuffer[i] = readShort(intArray, (i+1)*2);
	}
	
	var result = new Object();
	result.shortBuffer = shortBuffer;
	result.cmdStreamHeight = 0;
	
	return result;
}

function xhrSuccessHandler() {
	if (xmlhttpreq.readyState==4) {
	  if(xmlhttpreq.status==200) {
		  responseHandlerFunc();
	  } else {
		  //TODO: Alert
		  //startRequestFunc(subSessionID);
	  }
  }
}
