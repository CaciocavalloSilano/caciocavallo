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

package net.java.openjdk.cacio.servlet.transport;

import java.io.*;
import java.util.*;

public abstract class BinaryTransport extends Transport {

    byte[] emptyResponseData;
    
    public BinaryTransport() {
	super("application/binary");
	
	emptyResponseData = new byte[2];
	emptyResponseData[0] = 0;
	emptyResponseData[1] = 0;
    }
    
    protected byte[] encodeImageCmdStream(List<Integer> cmdList) {
	ByteArrayOutputStream bos = new ByteArrayOutputStream(cmdList.size()*2 + 2);
	
	writeJSShort(bos, cmdList.size());
	for(int value : cmdList) {
	    writeJSShort(bos, value);
	}

	return bos.toByteArray();
    }
    
    protected void writeJSShort(ByteArrayOutputStream bos, int value) {
	int sign = value >= 0 ? 0 : 1;
	int absValue = Math.abs(value);
	
	int highByte = ((absValue & 32512) >> 8) + (sign << 7);
	int lowByte = absValue & 0x000000FF;

	bos.write(highByte);
	bos.write(lowByte);
    }

    @Override
    public void writeEmptyData(OutputStream os) throws IOException {
	os.write(emptyResponseData);
    }
}
