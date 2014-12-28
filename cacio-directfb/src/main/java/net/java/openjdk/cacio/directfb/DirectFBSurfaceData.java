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
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

import sun.java2d.SurfaceData;
import sun.java2d.loops.SurfaceType;

public class DirectFBSurfaceData extends SurfaceData {

    static SurfaceType typeDefault =
            SurfaceType.IntRgb.deriveSubType("Cacio DirectFB default");

    private static native void initIds();

    static {
        initIds();
    }

    private native void initOps(long dfbSurface, int x, int y, int w, int h);

    DirectFBSurfaceData(SurfaceType surfaceType, ColorModel cm) {
        super(surfaceType, cm);
        DirectFBGraphicsConfiguration conf = DirectFBGraphicsConfiguration.getDefaultConfiguration();
        long surface = conf.getDirectFBSurface();
        Rectangle b = conf.getBounds();
        initOps(surface, b.x, b.y, b.width, b.height);
    }

    @Override
    public Rectangle getBounds() {
        return getDeviceConfiguration().getBounds();
    }

    @Override
    public Object getDestination() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return DirectFBGraphicsConfiguration.getDefaultConfiguration();
    }

    @Override
    public Raster getRaster(int arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SurfaceData getReplacement() {
        // TODO Auto-generated method stub
        return null;
    }

}
