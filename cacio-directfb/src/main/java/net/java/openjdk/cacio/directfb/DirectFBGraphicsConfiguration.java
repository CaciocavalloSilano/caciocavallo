/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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

package net.java.openjdk.cacio.directfb;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

import sun.awt.SunToolkit;

public class DirectFBGraphicsConfiguration extends GraphicsConfiguration {

    private DirectFBGraphicsDevice device;

    private long directFBSurface;

    private static native void initIDs();

    static {
        initIDs();
    }

    private native long getDirectFBSurface(long directfb);

    private native void getSurfaceBounds(long surface, DirectFBRect bounds);

    DirectFBGraphicsConfiguration(DirectFBGraphicsDevice device) {
        this.device = device;
        try {
            SunToolkit.awtLock();
            directFBSurface = getDirectFBSurface(device.getEnv().getDirectFB());
        } finally {
            SunToolkit.awtUnlock();
        }
    }

    @Override
    public GraphicsDevice getDevice() {
        return device;
    }

    @Override
    public ColorModel getColorModel() {
        return new DirectColorModel(32, 0x00FF0000, 0x0000FF00,
                0x000000FF);
    }

    @Override
    public ColorModel getColorModel(int transparency) {
        if (transparency == Transparency.OPAQUE) {
            return getColorModel();
        } else {
            return ColorModel.getRGBdefault();
        }
    }

    @Override
    public AffineTransform getDefaultTransform() {
        return new AffineTransform();
    }

    @Override
    public AffineTransform getNormalizingTransform() {
        throw new InternalError("NYI");
    }

    @Override
    public Rectangle getBounds() {
        DirectFBRect bounds = new DirectFBRect();
        try {
            SunToolkit.awtLock();
            getSurfaceBounds(directFBSurface, bounds);
            return new Rectangle(bounds.width, bounds.height);
        } finally {
            SunToolkit.awtUnlock();
        }
    }

    long getDirectFBSurface() {
        return directFBSurface;
    }

    static DirectFBGraphicsConfiguration getDefaultConfiguration() {
        DirectFBGraphicsEnvironment env = (DirectFBGraphicsEnvironment) GraphicsEnvironment.getLocalGraphicsEnvironment();
        DirectFBGraphicsDevice dev = (DirectFBGraphicsDevice) env.getDefaultScreenDevice();
        DirectFBGraphicsConfiguration conf = (DirectFBGraphicsConfiguration) dev.getDefaultConfiguration();
        return conf;
    }
}
