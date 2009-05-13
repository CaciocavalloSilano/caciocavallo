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
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.WindowPeer;

import javax.swing.JRootPane;
import javax.swing.border.Border;

class CacioWindowPeer extends CacioContainerPeer<Window, JRootPane>
                      implements WindowPeer {

    private static boolean decorateWindows = "true".equals(System.getProperty("cacio.decorateWindows", "true"));

    CacioWindowPeer(Window awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
        ((Window) awtC).setFocusableWindowState(true);
        ((Window) awtC).setFocusTraversalPolicyProvider(true);
    }

    @Override
    void init(PlatformWindowFactory pwf) {
        
        platformWindow = pwf.createPlatformToplevelWindow(this);

    }

    @Override
    JRootPane initSwingComponent() {
        JRootPane jrootpane;
        if (decorateWindows) {
            Window window = (Window) getAWTComponent();
            jrootpane = new JRootPane();
        } else {
            jrootpane = null;
        }
        return jrootpane;
    }

    @Override
    void postInitSwingComponent() {
        JRootPane jrootpane = getSwingComponent();
        if (jrootpane != null) {
            int deco = getRootPaneDecorationStyle();
            jrootpane.setWindowDecorationStyle(deco);
        }
    }

    protected int getRootPaneDecorationStyle() {
        return JRootPane.NONE;
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

    @Override
    public void handlePeerEvent(AWTEvent ev, EventPriority prio) {


        Window w = (Window) getAWTComponent();
        switch (ev.getID()) {

        case FocusEvent.FOCUS_GAINED:
            {
                WindowEvent we =
                    new WindowEvent(w, WindowEvent.WINDOW_GAINED_FOCUS);
                super.handlePeerEvent(we, prio);
                super.handlePeerEvent(ev, prio);
            }
            break;
        case FocusEvent.FOCUS_LOST:
            {
                super.handlePeerEvent(ev, prio);
                WindowEvent we =
                    new WindowEvent(w, WindowEvent.WINDOW_LOST_FOCUS);
                super.handlePeerEvent(we, prio);
            }
            break;
        default:
            super.handlePeerEvent(ev, prio);
        }
    }

    protected PlatformToplevelWindow getToplevelWindow() {
        return (PlatformToplevelWindow) platformWindow;
    }

    @Override
    public Insets getInsets() {
        if (decorateWindows) {
            JRootPane rp = getSwingComponent();
            if (rp == null) {
                return new Insets(0, 0, 0, 0);
            }
            if (! rp.isValid()) {
                rp.validate();
            }
            Component cp = rp.getContentPane();
            Rectangle cpBounds = cp.getBounds();
            Component lp = rp.getLayeredPane();
            Point lpLoc = lp.getLocation();
            int top = cpBounds.y + lpLoc.y;
            int left = cpBounds.x + lpLoc.x;
            Border b = rp.getBorder();
            int bottom;
            int right;
            if (b != null) {
                Insets bi = b.getBorderInsets(rp);
                bottom = bi.bottom;
                right = bi.right;
            } else {
                bottom = 0;
                right = 0;
            }
            Insets insets = new Insets(top, left, bottom, right);
            return insets;
        } else {
            return platformWindow.getInsets();
        }
    }

    @Override
    public void setOpacity(float opacity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateWindow(BufferedImage backBuffer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void repositionSecurityWarning() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
