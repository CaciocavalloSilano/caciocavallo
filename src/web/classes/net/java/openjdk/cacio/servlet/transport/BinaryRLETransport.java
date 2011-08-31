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
 * 
 * Transport using run-length encoding, data is transmitted in binary format.
 * This is the preferred backend for high-performance CPUs when bandwidth is not
 * constrained, like in corporate networks using desktop PCs because RLE
 * encoding puts only light load on the server, compared to PNG.
 * 
 * XHR2 is preferred, for compatibility a slow XHR1 based workarround exists.
 * 
 * Javascript counterpart: XHR2RLETransport.js, XHR1RLETransport.js,
 * RLEImageDecoder.js
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class BinaryRLETransport extends BinaryTransport {
    RLEImageEncoder rleEncoder;
    byte[] cmdStreamData;

    public BinaryRLETransport() {
    }

    @Override
    public void prepareUpdate(List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList) {
	cmdStreamData = encodeImageCmdStream(cmdList);

	WebRect packedRegionBox = packer.getBoundingBox();
	if (packedRegionBox.getWidth() == 0 || packedRegionBox.getHeight() == 0) {
	    return;
	}

	rleEncoder = new RLEImageEncoder();
	// Fast-Path: If there is only a single BlitScreenUpdate, encode
	// directly from the SurfaceData and avoid an additional blit
	BlitScreenUpdate singleUpdate = getLonelyBlitScreenUpdate(pendingUpdateList);
	if (singleUpdate != null) {
	    WebRect updateArea = singleUpdate.getUpdateArea();
	    rleEncoder.encodeImageToStream(singleUpdate.getImage(), singleUpdate.getSrcX(), singleUpdate.getSrcY(), singleUpdate.getSrcX()
		    + updateArea.getWidth(), singleUpdate.getSrcY() + updateArea.getHeight());
	} else {
	    BufferedImage packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	    copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	    rleEncoder.encodeImageToStream(packedImage, 0, 0, packedImage.getWidth(), packedImage.getHeight());
	}
    }

    @Override
    public void writeEncodedData(OutputStream os) throws IOException {
	os.write(cmdStreamData);

	if (rleEncoder != null) {
	    rleEncoder.writeTo(os);
	    rleEncoder = null;
	}
    }

    /**
     * Returns the one and only BlitScreenUpdate of a list, or null if the List
     * has more than one BlitScreenCommands
     * 
     * @param updateList
     * @return
     */
    protected BlitScreenUpdate getLonelyBlitScreenUpdate(List<ScreenUpdate> updateList) {
	BlitScreenUpdate lonelyUpdate = null;

	for (ScreenUpdate update : updateList) {
	    if (update instanceof BlitScreenUpdate) {
		// If there is more than one BlitScreenUpdate, bail out
		if (lonelyUpdate != null) {
		    lonelyUpdate = null;
		    break;
		}
		lonelyUpdate = (BlitScreenUpdate) update;
	    }
	}

	return lonelyUpdate;
    }
}
