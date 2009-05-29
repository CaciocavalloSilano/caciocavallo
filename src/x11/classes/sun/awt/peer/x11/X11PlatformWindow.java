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

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.peer.ContainerPeer;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.SunToolkit;
import sun.awt.peer.cacio.CacioComponent;
import sun.awt.peer.cacio.PlatformToplevelWindow;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.Region;

class X11PlatformWindow implements PlatformToplevelWindow {

    private long nativeWindow;

    private boolean toplevel;

    private boolean visible;

    private X11SurfaceData surfaceData;

    private CacioComponent cacioComponent;

    private native long nativeInit(long dpy, long parent, int x, int y, int w, int h);

    private native void nativeSetBounds(long dpy, long nw, int x, int y, int w, int h);

    private native void nativeSetVisible(long dpy, long nw, boolean v);

    /**
     * Creates a window without parent, i.e. a toplevel window.
     */
    X11PlatformWindow(X11EventPump pump, CacioComponent comp, int x, int y, int w, int h) {
        SunToolkit.awtLock();
        try {
            nativeWindow = nativeInit(X11GraphicsEnvironment.getDisplay(), 0L,
                                      x, y, w, h);
        } finally {
            SunToolkit.awtUnlock();
        }
        toplevel = true;
        cacioComponent = comp;
        pump.registerWindow(nativeWindow, this);
    }

    /**
     * Creates a window with parent, i.e. a nested window.
     */
    X11PlatformWindow(X11EventPump pump, CacioComponent comp, X11PlatformWindow parent, int x, int y, int w, int h) {
        SunToolkit.awtLock();
        try {
            nativeWindow = nativeInit(X11GraphicsEnvironment.getDisplay(),
                                      parent.nativeWindow, x, y, w, h);
        } finally {
            SunToolkit.awtUnlock();
        }
        toplevel = false;
        cacioComponent = comp;
        pump.registerWindow(nativeWindow, this);
    }

    CacioComponent getCacioComponent() {
        return cacioComponent;
    }

    public ColorModel getColorModel() {
        return getGraphicsConfiguration().getColorModel();
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return X11GraphicsConfiguration.getDefaultGC();
    }

    private Rectangle bounds = new Rectangle();
    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Graphics2D getGraphics() {
        X11SurfaceData sd = getSurfaceData();
        Graphics2D g2d = new SunGraphics2D(sd, Color.BLACK, Color.BLACK,
                                        new Font(Font.DIALOG, Font.PLAIN, 12));
        return g2d;
    }

    private X11SurfaceData getSurfaceData() {
        if (! visible) {
            System.err.println("STUPID");
            System.exit(0);
        }
        if (surfaceData == null) {
            surfaceData = new X11SurfaceData(X11SurfaceData.typeDefault,
                                             getColorModel(), bounds.width, bounds.height,
                                             getGraphicsConfiguration(), this,
                                             nativeWindow);
        }
        return surfaceData;
    }

    public void setBounds(int x, int y, int width, int height, int op) {
        bounds.x = x;
        bounds.y = y;
        bounds.width = Math.max(width, 1);
        bounds.height = Math.max(height, 1);
        SunToolkit.awtLock();
        try {
            nativeSetBounds(X11GraphicsEnvironment.getDisplay(), nativeWindow,
                            x, y, width, height);
        } finally {
            SunToolkit.awtUnlock();
        }
    }

    public Insets getInsets() {
        // TODO: Return correct insets...
        return new Insets(0, 0, 0, 0);
    }

    public Point getLocationOnScreen() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canDetermineObscurity() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isObscured() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void applyShape(Region shape) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isReparentSuppored() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reparent(ContainerPeer newContainer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRestackSupported() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void restack() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setVisible(boolean b) {
        SunToolkit.awtLock();
        try {
        nativeSetVisible(X11GraphicsEnvironment.getDisplay(), nativeWindow, b);
        } finally {
            SunToolkit.awtUnlock();
        }
        visible = b;
    }

    public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, Cause cause) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBackground(Color c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setForeground(Color c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFont(Font f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void destroyBuffers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Image getBackBuffer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void requestFocus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setState(int state) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setMaximizedBounds(Rectangle bounds) {
        System.err.println("TODO: Implement X11PlatformWindow.setMaximizedBounds()");
    }

    public void setResizable(boolean resizable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTitle(String title) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
