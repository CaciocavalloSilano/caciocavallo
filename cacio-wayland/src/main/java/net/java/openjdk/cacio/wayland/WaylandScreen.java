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

import sun.awt.peer.cacio.WindowClippedGraphics;
import sun.awt.peer.cacio.managed.PlatformScreen;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Area;
import java.awt.image.ColorModel;
import java.util.concurrent.atomic.AtomicLong;


class WaylandScreen implements PlatformScreen, WindowListener, ComponentListener{

    static {
        WaylandGraphicsConfiguration.getDefaultConfiguration();
    }


    private static final AtomicLong ScreenIdGenerator = new AtomicLong(0);

    private WaylandShmSurfaceData   surfaceData;
    private Component               comp;
    private Rectangle               bounds;
    private GraphicsConfiguration   config;

    // Screen Id
    private long                    id;


    WaylandScreen(Component comp) {
        this.comp = comp;
        this.id = ScreenIdGenerator.incrementAndGet();
        comp.setLocation(0, 0);
        this.comp.addComponentListener(this);
        if (comp instanceof Window) {
            ((Window)comp).addWindowListener(this);
        }
        this.config = WaylandGraphicsConfiguration.getDefaultConfiguration().cloneForScreen(this);
    }

    public long getId() {
        return id;
    }

    @Override
    public ColorModel getColorModel() {
        return getGraphicsConfiguration().getColorModel();
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        return config;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public Graphics2D getClippedGraphics(Color fg, Color bg, Font f, java.util.List<Rectangle> clipRects) {
        SurfaceData sd = getSurfaceData();
        Rectangle   surfBound = new Rectangle(getBounds());
        Graphics2D g2d = new SunGraphics2D(sd, fg, bg, f);

        if (clipRects != null && clipRects.size() > 0) {
            Area a = new Area(surfBound);
            for (Rectangle clip : clipRects) {
                a.subtract(new Area(clip));
            }
            g2d = new WindowClippedGraphics(g2d, a);
        } else {
            g2d.setClip(surfBound);
        }

        return g2d;
    }


    private void resizeSurface(Rectangle bounds) {
        // Could not resize surface in wayland level, so destroy current surface and recreate
        // the surface with desirable size
        if (this.surfaceData != null) {
            this.surfaceData.dispose();
        }

        this.surfaceData = new WaylandShmSurfaceData(this, WaylandShmSurfaceData.typeDefault, getColorModel(), bounds);
        this.bounds = new Rectangle(bounds);
    }

    private SurfaceData getSurfaceData() {
        if(comp.getX() != 0 || comp.getY() != 0) {
            comp.setLocation(0, 0);
        }

        if (this.surfaceData == null) {
           this.bounds = comp.getBounds();
           this.surfaceData = new WaylandShmSurfaceData(this, WaylandShmSurfaceData.typeDefault, getColorModel(), bounds);
        }
        return surfaceData;
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        resizeSurface(componentEvent.getComponent().getBounds());
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {
        componentEvent.getComponent().setLocation(0, 0);
    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {
        componentEvent.getComponent().setLocation(0, 0);
        if (this.surfaceData == null || !this.bounds.equals(componentEvent.getComponent().getBounds())) {
            resizeSurface(componentEvent.getComponent().getBounds());
        } else {
            this.surfaceData.remap();
        }
    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {
        if (this.surfaceData != null) {
            this.surfaceData.unmap();
        }
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {
        windowEvent.getComponent().setLocation(0, 0);
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
        WaylandToolkit.screenSelector.removeScreen(id);
        if (this.surfaceData != null) {
            this.surfaceData.dispose();
            this.surfaceData = null;
        }
        this.bounds = null;
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }
}
