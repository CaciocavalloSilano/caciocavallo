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
import sun.awt.peer.cacio.CacioComponent;
import sun.awt.peer.cacio.CacioEventPump;
import sun.awt.peer.cacio.PlatformWindow;
import sun.awt.peer.cacio.PlatformWindowFactory;

public class X11PlatformWindowFactory implements PlatformWindowFactory {

    public PlatformWindow createPlatformWindow(CacioComponent component, PlatformWindow parent) {
        Component awtC = component.getAWTComponent();
        return new X11PlatformWindow((X11PlatformWindow) parent,
                                     awtC.getX(), awtC.getY(), awtC.getWidth(),
                                     awtC.getHeight());
    }

    public PlatformWindow createPlatformToplevelWindow(CacioComponent component) {
        Component awtC = component.getAWTComponent();
        return new X11PlatformWindow(awtC.getX(), awtC.getY(), awtC.getWidth(),
                                     awtC.getHeight());
    }

    public CacioEventPump<?> createEventPump() {
        return new X11EventPump();
    }

}
