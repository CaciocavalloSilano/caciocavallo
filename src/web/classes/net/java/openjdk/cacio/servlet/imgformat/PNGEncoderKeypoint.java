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

import java.lang.reflect.*;
import java.util.logging.*;


/**
 * Reflection based implementation (to avoid compile-time dependency) of the
 * PNGEncoder interface, using the com.keypoint.PngEncoder png encoder, which is
 * faster than the ImageIO solution bundled with the JDK and allows
 * caciocavallo-web to modify the compression ratio.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class PNGEncoderKeypoint extends PNGEncoder {

    private static Logger logger = Logger.getLogger(PNGEncoderKeypoint.class.getName());
    
    private static Class<?> keypointEncoderCls;
    private static Constructor<?> keypointEncoderConstructor;
    private static Method keypointEncodeMethod;

    public PNGEncoderKeypoint() {
	loadKeypointEncoder();
    }

    /**
     * Loads the keypoint png encoder using reflection.
     */
    private void loadKeypointEncoder() {
	try {
	    keypointEncoderCls = Class.forName("com.keypoint.PngEncoderB");
	    keypointEncoderConstructor = keypointEncoderCls.getConstructor(new Class[] { BufferedImage.class, boolean.class, int.class, int.class });
	    keypointEncodeMethod = keypointEncoderCls.getMethod("pngEncode", new Class[0]);
	} catch (Exception ex) {
	    logger.log(Level.INFO, ex.getMessage());
	}
    }

    /**
     * @return true iff loading com.keypoint.PngEncoder was successful.
     */
    protected boolean isAvailable() {
	return keypointEncodeMethod != null;
    }

    /**
     * @see PNGEncoder
     */
    @Override
    public byte[] encode(BufferedImage img, int compression) {
	try {
	    Object encoderInstance = keypointEncoderConstructor.newInstance(new Object[] { img, Boolean.FALSE, Integer.valueOf(0), compression });
	    return (byte[]) keypointEncodeMethod.invoke(encoderInstance, new Object[0]);
	} catch (Exception ex) {
	    logger.log(Level.SEVERE, "Error encoding png image", ex);
	}

	return null;
    }
}
