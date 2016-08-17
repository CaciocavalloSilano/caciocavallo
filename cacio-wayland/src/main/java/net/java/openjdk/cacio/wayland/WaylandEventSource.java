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

import sun.awt.peer.cacio.CacioEventSource;
import sun.awt.peer.cacio.managed.EventData;
import sun.awt.peer.cacio.managed.PlatformScreen;

import java.awt.*;

public class WaylandEventSource implements CacioEventSource {
    private native void nativeGetEvent(EventData evt);
    private static native void initIDs();
    static {
        WaylandGraphicsConfiguration.getDefaultConfiguration();
        initIDs();
    }

    private WaylandScreenSelector screenSelector;

    public WaylandEventSource(WaylandScreenSelector selector) {
        this.screenSelector = selector;
    }

    @Override
    public EventData getNextEvent() throws InterruptedException {
        while(true) {
            EventData evt = new EventData();
            nativeGetEvent(evt);

            Object source = evt.getSource();
            if (source == null) {
                continue;
            }

            // map screen Id to corresponding PlatformScreen
            long screenId = (Long)evt.getSource();
            PlatformScreen screen = screenSelector.screenById(screenId);
            if (screen == null) {
                continue;
            }

            Rectangle screenBounds = screen.getBounds();
            if (screen.getBounds() != null) {
                evt.setX(evt.getX() + (int) screenBounds.getX());
                evt.setY(evt.getY() + (int) screenBounds.getY());
            }

            evt.setSource(screen);
            return evt;
        }
    }
}
