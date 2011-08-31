/*
 * Copyright (c) 2011, Clemens Eisserer,  Oracle and/or its affiliates. All rights reserved.
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
 * Transport sending binary data, with png used for image-encoding.
 * 
 * Because browsers don't provide a way to feed binary data into an image, the
 * client has to generate a base64 encoded data-URI.
 * 
 * Useful for systems where bandwith is limited, but the client has a powerful
 * CPU.
 * 
 * Javascript counterpart: XHR2PngTransport.js
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class BinaryPngTransport extends BinaryTransport {
    int compressionLevel;

    byte[] cmdStreamData;
    BufferedImage packedImage;

    public BinaryPngTransport(int compressionLevel) {
	this.compressionLevel = compressionLevel;
    }

    @Override
    public void prepareUpdate(List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList) {
	cmdStreamData = encodeImageCmdStream(cmdList);

	WebRect packedRegionBox = packer.getBoundingBox();
	if (packedRegionBox.getWidth() > 0 && packedRegionBox.getHeight() > 0) {
	    packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	    copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	}
    }

    @Override
    public void writeEncodedData(OutputStream os) throws IOException {
	os.write(cmdStreamData);

	if (packedImage != null) {
	    byte[] pngData = PNGEncoder.getInstance().encode(packedImage, compressionLevel);
	    packedImage = null;
	    os.write(pngData);
	}
    }
}
