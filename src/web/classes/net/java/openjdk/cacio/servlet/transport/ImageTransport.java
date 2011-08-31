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

import java.awt.image.*;
import java.io.*;
import java.util.*;

import net.java.openjdk.awt.peer.web.*;
import net.java.openjdk.cacio.servlet.imgformat.*;

/**
 * Transport encoding the command-stream directly in a png-image.
 * 
 * This has the advantage of not requiring a lot of javascript-processing of
 * image-data, the image can be loaded by the browser using native code - and
 * the bytes of the command-stream can be read from that image later.
 * 
 * Unfourtunatly Webkit based Browsers have a bug which causes a memory-leak,
 * furthermore it is not guaranteed that the cmd-stream data can be extracted
 * again with the required precision (this is tested at run-time by the
 * javascript-part of the backend).
 * 
 * The javascript-counterpart of this backend is ImgTransport.js
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class ImageTransport extends Transport {

    byte[] emptyImgData;
    int compressionLevel;

    BufferedImage packedImage;
    
    public ImageTransport(int compressionLevel) {
	super("image/png");
	this.compressionLevel = compressionLevel;

	BufferedImage emptyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	emptyImg.setRGB(0, 0, 0);
	emptyImgData = PNGEncoder.getInstance().encode(emptyImg, 2);
    }

    /**
     * Encodes the command-stream in the BufferedImage passed. The red byte is
     * simply used for the sign, while green+blue are used as 16 bit integer
     * value.
     * 
     * @param bImg
     * @param cmdList
     */
    protected void encodeImageCmdStream(BufferedImage bImg, List<Integer> cmdList) {
	bImg.setRGB(0, 0, cmdList.size());

	for (int i = 0; i < cmdList.size(); i++) {
	    int pixelCnt = i + 1;
	    int yPos = pixelCnt / bImg.getWidth();
	    int xPos = pixelCnt % bImg.getWidth();

	    int intValue = cmdList.get(i);
	    int r = intValue < 0 ? 1 << 16 : 0; // sign
	    int gb = Math.abs(intValue) & 0x0000FFFF;

	    int rgb = r | gb;
	    bImg.setRGB(xPos, yPos, rgb);
	}
    }

    @Override
    public void prepareUpdate(List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList) {
	WebRect packedRegionBox = packer.getBoundingBox();
	int regionWidth = packedRegionBox.getWidth() != 0 ? packedRegionBox.getWidth() : 16;
	int regionHeight = packedRegionBox.getHeight();
	int cmdAreaHeight = (int) Math.ceil(((double) cmdList.size() + 1) / (regionWidth));

	packedImage = new BufferedImage(regionWidth, regionHeight + cmdAreaHeight, BufferedImage.TYPE_INT_RGB);
	encodeImageCmdStream(packedImage, cmdList);
	copyUpdatesToPackedImage(pendingUpdateList, packedImage, cmdAreaHeight);
    }
    
    @Override
    public void writeEncodedData(OutputStream os) throws IOException {
	byte[] imgData = PNGEncoder.getInstance().encode(packedImage, compressionLevel);
	os.write(imgData);
	packedImage = null;
    }

    @Override
    public void writeEmptyData(OutputStream os) throws IOException {
	os.write(emptyImgData);
    }

}
