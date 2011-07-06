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
package net.java.openjdk.awt.peer.web;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.ColorModel;
import java.util.*;

import sun.awt.peer.cacio.CacioEventSource;
import sun.awt.peer.cacio.WindowClippedGraphics;
import sun.awt.peer.cacio.managed.*;
import sun.java2d.SunGraphics2D;

/**
 *
 * @author Mario Torre <neugens@limasoftware.net>
 */
public class WebScreen implements PlatformScreen {

    private static final int width;
    private static final int height;
   
    WebGraphicsConfiguration config;
    
    static {
        Dimension dim = FullScreenWindowFactory.getScreenDimension();
        width = dim.width;
        height = dim.height;
    }

    private EventData eventData;
    private WebSurfaceData surfaceData;

    protected WebScreen(WebGraphicsConfiguration config) {
	this.config = config;
    }

    public Graphics2D getClippedGraphics(Color fg, Color bg, Font font,
                                         List<Rectangle> clipRects) {

        WebSurfaceData data = getSurfaceData();
        Graphics2D g2d = new SunGraphics2D(data, fg, bg, font);
        if (clipRects != null && clipRects.size() > 0) {
            Area a = new Area(getBounds());
            for (Rectangle clip : clipRects) {
                a.subtract(new Area(clip));
            }
            g2d = new WindowClippedGraphics(g2d, a);
        }
        return g2d;
    }

    public ColorModel getColorModel() {

        return getGraphicsConfiguration().getColorModel();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return config;
    }

    public Rectangle getBounds() {
        return new Rectangle(0, 0, width, height);
    }
    
    public synchronized void addEvent(EventData data) {
	WebToolkit toolkit = ((WebToolkit) WebToolkit.getDefaultToolkit());
	WebWindowFactory factory = (WebWindowFactory) toolkit.getPlatformWindowFactory();
	ScreenManagedWindowContainer windowContainer = factory.getScreenManagedWindowContainer(this);
	data.setSource(windowContainer);
	windowContainer.dispatchEvent(data);
    }

    public WebSurfaceData getSurfaceData() {

        if (surfaceData == null) {
            surfaceData = new WebSurfaceData(WebSurfaceData.typeDefault,
                                             getColorModel(), getBounds(),
                                             getGraphicsConfiguration(), this);
        }
        return surfaceData;
    }

    private native final long nativeInitScreen(int width, int height);

    public WebGraphicsConfiguration getConfig() {
        return config;
    }
}
