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

function initXHR1Rle() {
	startRequestFunc = StartXHR1RleRequest;
	readCmdStreamFunc = readBinCommandStream;
	responseHandlerFunc = handleXHR1RLEResponse;
	return "rle";
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

