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
package net.java.openjdk.awt.peer.sdl;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Transparency;
import sun.awt.SunToolkit;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.SurfaceData;

/**
 * @author Mario Torre <neugens.limasoftware@gmail.com>
 */
class SDLVolativeSurfaceManager extends VolatileSurfaceManager {

    SDLVolativeSurfaceManager(SunVolatileImage image, Object context) {

        super(image, context);
    }

    @Override
    protected boolean isAccelerationEnabled() {

       return vImg.getTransparency() == Transparency.OPAQUE;
    }

    @Override
    protected SurfaceData initAcceleratedSurface() {

        int width = vImg.getWidth();
        int height = vImg.getHeight();
        GraphicsConfiguration gc = vImg.getGraphicsConfig();
        SunToolkit.awtLock();
        try {

            long nativeSDLdata = initSurface(width, height);
            SDLSurfaceData surfaceData =
                    new SDLSurfaceData(SDLSurfaceData.typeDefault,
                                       gc.getColorModel(),
                                       new Rectangle(0, 0, width, height),
                                       vImg.getGraphicsConfig(), vImg,
                                       nativeSDLdata);

            return surfaceData;

        } finally {
            SunToolkit.awtUnlock();
        }
    }

    private native final long initSurface(int width, int height);
}
