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

package net.java.openjdk.cacio.servlet.imgformat;

import java.awt.image.*;

/**
 * Generic PNGEncoder interface, to allow Caciocavallo-Web to use different png
 * encoders without hard-coded dependencies. For now tries to loads the
 * "PngEncoderKeypoint" by default and if not available, falls back to an
 * ImageIO based implementation.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public abstract class PNGEncoder {

    private static PNGEncoder instance;

    public static synchronized PNGEncoder getInstance() {
	if (instance == null) {
	    PNGEncoderKeypoint keypointEncoder = new PNGEncoderKeypoint();
	    if (keypointEncoder.isAvailable()) {
		instance = keypointEncoder;
	    } else {
		instance = new PNGEncoderImageIO();
		System.out.println("Keypoint PNG-Encoder not found, falling back to ImageIO.");
	    }

	}

	return instance;
    }

    /**
     * Encodes the passed BufferedImage to png-format.
     * @param img
     * @param compression
     * @return
     */
    public abstract byte[] encode(BufferedImage img, int compression);
}
