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

package com.github.caciocavallosilano.cacio.peer;

import java.awt.Component;
import java.awt.Event;
import java.awt.PopupMenu;
import java.awt.peer.PopupMenuPeer;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

class CacioPopupMenuPeer extends CacioMenuPeer implements PopupMenuPeer {

    CacioPopupMenuPeer(PopupMenu m) {
        super(m);
    }

    public void show(Event e) {
        JMenu m = (JMenu) getSwingMenu();
        JPopupMenu pm = m.getPopupMenu();
        // Delegating to JPopupMenu.show registers the popup with
        // MenuSelectionManager, which dismisses it on outside clicks.
        // Calling setVisible(true) directly skips that and leaves the
        // popup pinned open (issue #17).
        pm.show((Component) e.target, e.x, e.y);
    }

}
