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

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.List;

import net.java.openjdk.awt.peer.web.*;

public abstract class Transport {
    
    public static final String FORMAT_PNG_BASE64 = "base64";
    public static final String FORMAT_PNG_IMG = "img";
    public static final String FORMAT_PNG_XHR = "png";
    public static final String FORMAT_RLE = "rle";
    
    String contentType;
    
    public Transport(String contentType) {
	this.contentType = contentType;
    }
    
    protected void copyUpdatesToPackedImage(List<ScreenUpdate> updateList, BufferedImage packedImage, int packedAreaHeight) {
	Graphics g = packedImage.getGraphics();

	for (ScreenUpdate update : updateList) {
	    if (update instanceof BlitScreenUpdate) {
		BlitScreenUpdate bsUpdate = (BlitScreenUpdate) update;

		int width = bsUpdate.getUpdateArea().getWidth();
		int height = bsUpdate.getUpdateArea().getHeight();

		g.drawImage(bsUpdate.getImage(), bsUpdate.getPackedX(), bsUpdate.getPackedY() + packedAreaHeight, bsUpdate.getPackedX() + width, bsUpdate.getPackedY()+ height + packedAreaHeight, bsUpdate.getSrcX(), bsUpdate.getSrcY(),
			bsUpdate.getSrcX() + width, bsUpdate.getSrcY() + height, null);
	    }
	}
    }
    
    public abstract void writeEncodedData(OutputStream os, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdData) throws IOException;
    
    public abstract void writeEmptyData(OutputStream os) throws IOException;

    public String getContentType() {
        return contentType;
    }
    
    public static Transport getBackendForName(String backendName, int compressionLevel) {
	if(backendName.equalsIgnoreCase(FORMAT_RLE)) {
	    return new BinaryRLETransport();
	} else if(backendName.equalsIgnoreCase(FORMAT_PNG_XHR)) {
	    return new BinaryPngTransport(compressionLevel);
	} else if(backendName.equalsIgnoreCase(FORMAT_PNG_IMG)) {
	    return new ImageTransport(compressionLevel);
	} else {
	    return new Base64PngTransport(compressionLevel);
	}
    }
}
