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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import sun.awt.SunToolkit;
import sun.awt.peer.cacio.CacioComponent;
import sun.awt.peer.cacio.CacioEventPump;

class X11EventPump extends CacioEventPump<X11EventData> {

    static {
        initIDs();
    }

    private X11EventData eventData;

    private HashMap<Long,X11PlatformWindow> windowMap;

    private static native void initIDs();

    private native void nativeFetchEvent(long dpy, X11EventData ed);

    X11EventPump() {
        eventData = new X11EventData();
        windowMap = new HashMap<Long,X11PlatformWindow>();
    }

    @Override
    protected X11EventData fetchNativeEvent() {
        eventData.clear();
        SunToolkit.awtLock();
        try {
            nativeFetchEvent(X11GraphicsEnvironment.getDisplay(), eventData);
        } finally {
            SunToolkit.awtUnlock();
        }
        if (eventData.getType() == X11EventData.NONE) {
            try { Thread.sleep(10); } catch (InterruptedException ex) { }
        }
        return eventData;
    }

    @Override
    protected void dispatchNativeEvent(X11EventData nativeEvent) {
        switch (nativeEvent.getType()) {
            case X11EventData.NONE:
                break;
            case X11EventData.MAP_NOTIFY:
                break;
            case X11EventData.EXPOSE: {
                X11PlatformWindow w = windowMap.get(Long.valueOf(nativeEvent.getWindow()));
                CacioComponent source = w.getCacioComponent();
                Component c = source.getAWTComponent();
                postPaintEvent(source, 0, 0, c.getWidth(), c.getHeight());
                break;
            }
            case X11EventData.MOTION: {
                X11PlatformWindow w = windowMap.get(Long.valueOf(nativeEvent.getWindow()));
                CacioComponent source = w.getCacioComponent();
                Component c = source.getAWTComponent();
                postMouseEvent(source, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, nativeEvent.getX(), nativeEvent.getY(), 0, false);
                break;
            }
            case X11EventData.BUTTON_PRESS: {
                X11PlatformWindow w = windowMap.get(Long.valueOf(nativeEvent.getWindow()));
                CacioComponent source = w.getCacioComponent();
                Component c = source.getAWTComponent();
                /* TODO: Fix mods. */
                System.err.println("BUTTON_PRESS");
                postMouseEvent(source, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), MouseEvent.BUTTON1_DOWN_MASK, nativeEvent.getX(), nativeEvent.getY(), 0, false);
                break;
            }
            case X11EventData.BUTTON_RELEASE: {
                X11PlatformWindow w = windowMap.get(Long.valueOf(nativeEvent.getWindow()));
                CacioComponent source = w.getCacioComponent();
                Component c = source.getAWTComponent();
                /* TODO: Fix mods. */
                System.err.println("BUTTON_RELEASE");
                postMouseEvent(source, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, nativeEvent.getX(), nativeEvent.getY(), 0, false);
                break;
            }
            default:
                System.err.println("unhandled event type: " + nativeEvent.getType());
        }
    }

    void registerWindow(long windowId, X11PlatformWindow w) {
        windowMap.put(Long.valueOf(windowId), w);
    }

    void deregisterWindow(long windowId) {
        windowMap.remove(Long.valueOf(windowId));
    }
}
