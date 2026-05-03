/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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
package com.github.caciocavallosilano.cacio.ctc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.List;

import com.github.caciocavallosilano.cacio.peer.WindowClippedGraphics;
import com.github.caciocavallosilano.cacio.peer.managed.FullScreenWindowFactory;
import com.github.caciocavallosilano.cacio.peer.managed.PlatformScreen;


public class CTCScreen implements PlatformScreen {

    private BufferedImage screenBuffer;

    private static CTCScreen instance;

    static CTCScreen getInstance() {
        if (instance == null) {
            instance = new CTCScreen();
        }
        return instance;
    }

    private CTCScreen() {
        Dimension d = FullScreenWindowFactory.getScreenDimension();
        screenBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public ColorModel getColorModel() {
        return screenBuffer.getColorModel();
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }

    @Override
    public Rectangle getBounds() {
        Dimension d = FullScreenWindowFactory.getScreenDimension();
        return new Rectangle(0, 0, d.width, d.height);
    }

    @Override
    public Graphics2D getClippedGraphics(Color fg, Color bg, Font f,
            List<Rectangle> clipRects) {
        Graphics2D g2d = (Graphics2D) screenBuffer.getGraphics();
        if (clipRects != null && clipRects.size() > 0) {
            Area a = new Area(getBounds());
            for (Rectangle clip : clipRects) {
                a.subtract(new Area(clip));
            }
            g2d = new WindowClippedGraphics(g2d, a);
        }
        return g2d;
    }

    int[] getRGBPixels(Rectangle bounds) {
        return screenBuffer.getRGB(bounds.x, bounds.y, bounds.width, bounds.height, null, 0, bounds.width);
    }

}
