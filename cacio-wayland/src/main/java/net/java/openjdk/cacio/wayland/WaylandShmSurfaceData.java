/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
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
package net.java.openjdk.cacio.wayland;

import sun.java2d.SurfaceData;
import sun.java2d.loops.SurfaceType;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

class WaylandShmSurfaceData extends SurfaceData {
    static SurfaceType typeDefault =
            SurfaceType.IntRgb.deriveSubType("Cacio Wayland default");

    static {
        initIds();
    }

    private native void initOps(long surface);
    private static native void initIds();

    private long          surface;
    private WaylandScreen screen;
    private Rectangle     bounds;

    WaylandShmSurfaceData(WaylandScreen screen, SurfaceType surfaceType, ColorModel cm, Rectangle rect) {
        super(surfaceType, cm);
        this.bounds = rect;
        this.screen = screen;
        this.surface = WaylandSurface.createShmScreenSurface(screen.getId(), 0, 0, bounds.width, bounds.height, 4);
        if (this.surface == 0) {
            throw new RuntimeException("Can not create screen surface");
        }

        initOps(this.surface);
    }

    final void dispose() {
        WaylandSurface.dispose(surface);
    }
    final void unmap() { WaylandSurface.unmap(surface);}
    final void remap() { WaylandSurface.remap(surface); }

    @Override
    public SurfaceData getReplacement() {
        return null;
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return WaylandGraphicsConfiguration.getDefaultConfiguration();
    }

    @Override
    public Raster getRaster(int i, int i1, int i2, int i3) {
        return null;
    }

    @Override
    public Rectangle getBounds() {
        return this.bounds;
    }

    @Override
    public Object getDestination() {
        return null;
    }
}
