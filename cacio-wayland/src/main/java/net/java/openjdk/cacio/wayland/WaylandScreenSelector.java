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

import sun.awt.peer.cacio.CacioComponent;
import sun.awt.peer.cacio.managed.PlatformScreen;
import sun.awt.peer.cacio.managed.PlatformScreenSelector;

import java.awt.*;
import java.util.*;

class WaylandScreenSelector  implements PlatformScreenSelector {
    private final Map<Component, WaylandScreen> map =
        Collections.synchronizedMap(new HashMap<>());

    public PlatformScreen getPlatformScreen(CacioComponent comp) {
        Component c = comp.getAWTComponent();
        c = getTopLevelComponent(c);

        WaylandScreen screen = map.get(c);
        if (screen == null) {
            screen = new WaylandScreen(c);
            map.put(c, screen);
        }
        return screen;
    }

    private Component getTopLevelComponent(Component c) {
        while (c.getParent() != null) {
            c = c.getParent();
        }
        return c;
    }

    public PlatformScreen screenById(long id) {
        Collection<WaylandScreen> c = map.values();
        for (WaylandScreen screen: c) {
            if (screen.getId() == id) return screen;
        }
        return null;
    }

    public void removeScreen(long id) {
        map.remove(id);
    }

    @Override
    public PlatformScreen getPlatformScreen(GraphicsConfiguration config) {
        return null;
    }
}
