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

import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxMenuItemPeer;
import javax.swing.JCheckBoxMenuItem;

class CacioCheckboxMenuItemPeer extends CacioMenuItemPeer
                                implements CheckboxMenuItemPeer {

    private class ProxyListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            // Update the state of the AWT menu item.
            ((CheckboxMenuItem) getAWTMenu())
                    .setState(e.getStateChange() == ItemEvent.SELECTED);
            // Notify all the AWT listeners.
            CheckboxMenuItem i = ((CheckboxMenuItem) getAWTMenu());
            ItemListener[] l = i.getItemListeners();
            if (l != null && l.length > 0) {
                for (int idx = 0; idx < l.length; idx++) {
                    ItemEvent ev = new ItemEvent(i, e.getID(), i,
                                                 e.getStateChange());
                    l[idx].itemStateChanged(ev);
                }
            }
        }

    }

    CacioCheckboxMenuItemPeer(MenuItem i) {
        super(i, new JCheckBoxMenuItem());
    }

    @Override
    void postInitSwingComponent() {
        super.postInitSwingComponent();
        setState(((CheckboxMenuItem) getAWTMenu()).getState());
        ProxyListener pl = new ProxyListener();
        getSwingMenu().addItemListener(pl);
    }

    @Override
    boolean needActionProxy() {
        return false;
    }


    public void setState(boolean t) {
        ((JCheckBoxMenuItem) getSwingMenu()).setState(t);
    }
}
