/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.WindowPeer;

class CacioWindowPeer extends CacioContainerPeer implements WindowPeer {

    CacioWindowPeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
        ((Window) awtC).setFocusableWindowState(true);
    }

    void init(PlatformWindowFactory pwf) {
        
        platformWindow = pwf.createPlatformToplevelWindow(this);

        initSwingComponent();
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        // TODO Auto-generated method stub

    }

    public void setModalBlocked(Dialog blocker, boolean blocked) {
        // TODO Auto-generated method stub

    }

    public void toBack() {
        // TODO Auto-generated method stub

    }

    public void toFront() {
        // TODO Auto-generated method stub

    }

    public void updateFocusableWindowState() {
        // Nothing to do here for now.
    }

    public void updateIconImages() {
        // TODO Auto-generated method stub

    }

    public void updateMinimumSize() {
        // TODO Auto-generated method stub

    }

    public void handlePeerEvent(AWTEvent ev) {
        // If we receive a FOCUS_GAINED event, we also need to send a
        // WINDOW_FOCUS_GAINED event.
        if (ev.getID() == FocusEvent.FOCUS_GAINED) {
            WindowEvent we = new WindowEvent((Window) getAWTComponent(),
                                             WindowEvent.WINDOW_GAINED_FOCUS);
            super.handlePeerEvent(we);
        }
        super.handlePeerEvent(ev);
    }

    protected PlatformToplevelWindow getToplevelWindow() {
        return (PlatformToplevelWindow) platformWindow;
    }
}
