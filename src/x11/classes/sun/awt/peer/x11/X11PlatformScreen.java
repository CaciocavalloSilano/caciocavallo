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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.util.List;

import sun.awt.peer.cacio.CacioEventSource;
import sun.awt.peer.cacio.EventData;
import sun.awt.peer.cacio.FullScreenWindowFactory;
import sun.awt.peer.cacio.PlatformScreen;
import sun.java2d.SunGraphics2D;

class X11PlatformScreen implements PlatformScreen, CacioEventSource {

    static {
        initIDs();
    }

    private X11SurfaceData surfaceData;

    private int width, height;

    private long window;

    private EventData eventData;

    private native static void initIDs();

    private native long nativeInitScreen(long display, int width, int height);

    private native void nativeGetEvent(long display, EventData ed);

    X11PlatformScreen() {
        Dimension dim = FullScreenWindowFactory.getScreenDimension();
        this.width = dim.width;
        this.height = dim.height;
        window = nativeInitScreen(X11GraphicsEnvironment.getDisplay(),
                                  width, height);
    }

    public Graphics2D getClippedGraphics(List<Rectangle> clipRects) {
        X11SurfaceData sd = getSurfaceData();
        Graphics2D g2d = new SunGraphics2D(sd, Color.BLACK, Color.BLACK,
                                        new Font(Font.DIALOG, Font.PLAIN, 12));
        // TODO: Implement the clipping part.
        return g2d;
    }

    private X11SurfaceData getSurfaceData() {
        if (surfaceData == null) {
            surfaceData = new X11SurfaceData(X11SurfaceData.typeDefault,
                                             getColorModel(), getBounds(),
                                             getGraphicsConfiguration(), this,
                                             window);
        }
        return surfaceData;
    }

    public ColorModel getColorModel() {
        return getGraphicsConfiguration().getColorModel();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return X11GraphicsConfiguration.getDefaultGC();
    }

    public Rectangle getBounds() {
        return new Rectangle(0, 0, width, height);
    }

    public EventData getNextEvent() {
        if (eventData == null) {
            eventData = new EventData();
        }
        eventData.clear();
        nativeGetEvent(X11GraphicsEnvironment.getDisplay(), eventData);
        if (eventData.getId() == 0) {
            try { Thread.sleep(100); } catch (Exception ex) {}
        }
        eventData.setSource(this);
        return eventData;
    }

}
