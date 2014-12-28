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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.ColorModel;
import java.util.List;

import sun.awt.peer.cacio.WindowClippedGraphics;
import sun.awt.peer.cacio.managed.PlatformScreen;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class DirectFBScreen implements PlatformScreen {

    private SurfaceData surfaceData;

    @Override
    public Rectangle getBounds() {
        return getGraphicsConfiguration().getBounds();
    }

    @Override
    public Graphics2D getClippedGraphics(Color fg, Color bg, Font font, List<Rectangle> clipRects) {
        SurfaceData sd = getSurfaceData();
        Graphics2D g2d = new SunGraphics2D(sd, fg, bg, font);
        if (clipRects != null && clipRects.size() > 0) {
            Area a = new Area(getBounds());
            for (Rectangle clip : clipRects) {
                a.subtract(new Area(clip));
            }
            g2d = new WindowClippedGraphics(g2d, a);
        }
        return g2d;

    }

    @Override
    public ColorModel getColorModel() {
        return getGraphicsConfiguration().getColorModel();
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        return DirectFBGraphicsConfiguration.getDefaultConfiguration();
    }

    private SurfaceData getSurfaceData() {
        if (surfaceData == null) {
            surfaceData = new DirectFBSurfaceData(DirectFBSurfaceData.typeDefault, getColorModel());
        }
        return surfaceData;
    }
}
