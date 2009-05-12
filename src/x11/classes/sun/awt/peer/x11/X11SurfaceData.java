/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.awt.peer.x11;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import sun.java2d.SurfaceData;
import sun.java2d.loops.SurfaceType;

class X11SurfaceData extends SurfaceData {

    static {
        initIDs();
    }

    // TODO: Implement all the surface types.
    static SurfaceType typeDefault =
            SurfaceType.IntRgb.deriveSubType("Cacio X11 default");

    private Rectangle bounds;

    private GraphicsConfiguration configuration;

    private Object destination;

    private static native void initIDs();

    private native void initOps(long display, long drawable);

    X11SurfaceData(SurfaceType surfaceType, ColorModel cm, Rectangle b,
                   GraphicsConfiguration gc, Object dest, long drawable) {

        super(surfaceType, cm);
        bounds = b;
        configuration = gc;
        destination = dest;
        initOps(X11GraphicsEnvironment.getDisplay(), drawable);

    }

    @Override
    public SurfaceData getReplacement() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return configuration;
    }

    @Override
    public Raster getRaster(int arg0, int arg1, int arg2, int arg3) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    @Override
    public Object getDestination() {
        return destination;
    }

}
