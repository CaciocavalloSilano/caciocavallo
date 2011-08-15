/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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

package net.java.openjdk.cacio.servlet.imgformat;

import java.io.*;
import java.util.*;

public class DynamicByteBuffer {
    final static int BUFFER_SIZE = 4096;
    
    ArrayList<byte[]> bufferList = new ArrayList<byte[]>();
    
    int curBufferPos = 0;
    byte[] curBuffer;
    
    public DynamicByteBuffer() {
	addBuffer();
    }
    
    public final void write(byte b) {
	if(curBufferPos == curBuffer.length) {
	    addBuffer();
	}
	
	curBuffer[curBufferPos] = b;
	curBufferPos++;
    }
    
    private void addBuffer() {
	curBuffer = new byte[BUFFER_SIZE];
	curBufferPos = 0;
	bufferList.add(curBuffer);
    }
    
    public int size() {
	return (bufferList.size() - 1) * BUFFER_SIZE + curBufferPos;
    }
    
    public void writeTo(OutputStream os) throws IOException {
	int size = size();
	for(int i=0; i < bufferList.size(); i++) {
	    int curBuffLength = Math.min(size - BUFFER_SIZE*i, BUFFER_SIZE);
	    byte[] curBuff = bufferList.get(i);
	    os.write(curBuff, 0, curBuffLength);
	}
    }
    
    
}
