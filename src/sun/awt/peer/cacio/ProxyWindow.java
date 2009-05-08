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

package sun.awt.peer.cacio;

import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;

/**
 * A specialized Window implementation with the sole purpose of providing
 * the Swing components that back up the AWT widgets a heavyweight parent.
 * From the point of view of the Swing component, this window acts exactly
 * like a usual heavyweight window. But it is infact just a proxy to
 * the AWT widget. This behaviour is achieved by overriding certain methods,
 * and by a special WindowPeer implementation in {@link ProxyWindow}.
 */
public class ProxyWindow extends Window {

    private CacioComponentPeer target;

    ProxyWindow(CacioComponentPeer t, JComponent c) {
        super(null);
        target = t;
        add(c);
    }

    CacioComponentPeer getTargetPeer() {
        return target;
    }

    void handleFocusEvent(FocusEvent e) {
        // TODO: Retarget?
        processFocusEvent(e);
    }

    void handleKeyEvent(KeyEvent e) {
        // TODO: Retarget?
        processKeyEvent(e);
    }

    void handleMouseEvent(MouseEvent e) {
        MouseEvent me = new MouseEvent(this, e.getID(), e.getWhen(),
                                       e.getModifiers(), e.getX(), e.getY(),
                                       e.getXOnScreen(), e.getYOnScreen(),
                                       e.getClickCount(), e.isPopupTrigger(),
                                       e.getButton());
        dispatchEvent(e);
        // TODO: maybe processMouseEvent(e); ??
    }

    void handleMouseMotionEvent(MouseEvent e) {
        MouseEvent me = new MouseEvent(this, e.getID(), e.getWhen(),
                                       e.getModifiers(), e.getX(), e.getY(),
                                       e.getXOnScreen(), e.getYOnScreen(),
                                       e.getClickCount(), e.isPopupTrigger(),
                                       e.getButton());
        dispatchEvent(e);
        // TODO: maybe processMouseEvent(e); ??
    }

}
