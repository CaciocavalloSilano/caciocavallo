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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.util.List;

import sun.awt.peer.cacio.CacioEventSource;
import sun.awt.peer.cacio.managed.EventData;
import sun.awt.peer.cacio.managed.FullScreenWindowFactory;
import sun.awt.peer.cacio.managed.PlatformScreen;
import sun.java2d.SunGraphics2D;

/**
 *
 * @author Mario Torre <neugens@limasoftware.net>
 */
class SDLScreen implements PlatformScreen, CacioEventSource {

    private static final int width;
    private static final int height;

    static {
        initIDs();
        
        Dimension dim = FullScreenWindowFactory.getScreenDimension();
        width = dim.width;
        height = dim.height;
    }

    private long nativeSDLdata = 0L;
    private EventData eventData = null;
    private SDLSurfaceData surfaceData = null;

    SDLScreen() {
        nativeSDLdata = nativeInitScreen(width, height);
    }

    public Graphics2D getClippedGraphics(List<Rectangle> clipRects) {

        SDLSurfaceData data = getSurfaceData();
        Graphics2D g2d = new SunGraphics2D(data,
                                           Color.BLACK,
                                           Color.BLACK,
                                           new Font(Font.DIALOG,
                                                    Font.PLAIN, 12));
        // TODO: Implement the clipping part.
        return g2d;
    }

    public ColorModel getColorModel() {

        return getGraphicsConfiguration().getColorModel();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {

        return SDLGraphicsConfiguration.getDefaultConfiguration();
    }

    public Rectangle getBounds() {
        return new Rectangle(0, 0, width, height);
    }

    public EventData getNextEvent() {

        if (eventData == null) {
            eventData = new EventData();
        }

        eventData.clear();
        
        nativeGetEvent(eventData);

        if (eventData.getId() == 0) {
            try {
                Thread.sleep(100);
            } catch (Exception ex) { /* nothing to do */ }
        }
        eventData.setSource(this);
        return eventData;
    }

    private SDLSurfaceData getSurfaceData() {

        if (surfaceData == null) {
            surfaceData = new SDLSurfaceData(SDLSurfaceData.typeDefault,
                                             getColorModel(), getBounds(),
                                             getGraphicsConfiguration(), this,
                                             nativeSDLdata);
        }
        return surfaceData;
    }

    private native final void nativeGetEvent(EventData eventData);
    private native final long nativeInitScreen(int width, int height);
    private native final static void initIDs();
}
