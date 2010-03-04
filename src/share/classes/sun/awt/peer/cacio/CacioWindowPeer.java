/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.peer.WindowPeer;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

class CacioWindowPeer extends CacioContainerPeer<Window, JRootPane>
                      implements WindowPeer {

    private static boolean decorateWindows = false;

    private static final Font defaultFont = new Font(Font.DIALOG, Font.PLAIN, 12);

    static void setDecorateWindows(boolean decorate) {
        decorateWindows = decorate;
    }

    static boolean isDecorateWindows() {
        return decorateWindows;
    }

    CacioWindowPeer(Window awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
        ((Window) awtC).setFocusableWindowState(true);
        ((Window) awtC).setFocusTraversalPolicyProvider(true);
    }

    @Override
    void init(PlatformWindowFactory pwf) {

        Window w = getAWTComponent();
        Component parentComp = w.getParent();

        if (parentComp != null) {

            CacioComponentPeer parentPeer =
                    (CacioComponentPeer) parentComp.getPeer();

            PlatformWindow owner = parentPeer.platformWindow;
            platformWindow = pwf.createPlatformToplevelWindow(this, owner);
            
        } else {

            platformWindow = pwf.createPlatformToplevelWindow(this);
        }

        if (! w.isForegroundSet()) {
           // TODO: Use SystemColor here, and load the correct colors in the
           // Toolkit.
           w.setForeground(UIManager.getColor("windowText"));
           // w.setForeground(SystemColor.windowText);
        }
        if (! w.isBackgroundSet()) {
            // TODO: as above. The color thingy is a bit weird, because if we
            // use Panel.background we get an ugly white color for some
            // components and the nice swing color for other.
            Color c = UIManager.getColor("window");
            if (c != null) {
                c = UIManager.getColor("Panel.background");
            }
            w.setBackground(c);
        }

        if (! w.isFontSet()) {
            w.setFont(defaultFont);
        }
    }

    @Override
    JRootPane initSwingComponent() {
        // We always need a rootPane, even for undecorated windows, otherwise
        // we cannot have menu support...
        Window window = (Window) getAWTComponent();
        JRootPane jrootpane = new JRootPane();
        return jrootpane;
    }

    @Override
    void postInitSwingComponent() {
        super.postInitSwingComponent();
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
    public void handlePeerEvent(AWTEvent ev) {
        
        Window w = (Window) getAWTComponent();
        switch (ev.getID()) {

        case FocusEvent.FOCUS_GAINED:
            {
                WindowEvent we =
                    new WindowEvent(w, WindowEvent.WINDOW_GAINED_FOCUS);
                super.handlePeerEvent(we);
                super.handlePeerEvent(ev);
            }
            break;
        case FocusEvent.FOCUS_LOST:
            {
                super.handlePeerEvent(ev);
                WindowEvent we =
                    new WindowEvent(w, WindowEvent.WINDOW_LOST_FOCUS);
                super.handlePeerEvent(we);
            }
            break;
        default:
            super.handlePeerEvent(ev);
        }
    }

    protected PlatformToplevelWindow getToplevelWindow() {
        return (PlatformToplevelWindow) platformWindow;
    }

    @Override
    boolean hasInsets() {
        return true;
    }

    @Override
    public Insets getInsets() {
        Insets insets;

        if (isDecorateWindows()) {
            JRootPane rp = getSwingComponent();
            if (rp == null) {
                return new Insets(0, 0, 0, 0);
            }
            // Need to make the proxy visible, otherwise the root pane is
            // not laid out. Making the proxy visible has no effect as
            // as long as the platform window is hidden.
            ProxyWindow proxy = getProxyWindow();
            if (! proxy.isVisible()) {
                proxy.setVisible(true);
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
            insets = new Insets(top, left, bottom, right);
        } else {
            insets = (Insets) platformWindow.getInsets().clone();
            // here, We need to hande the menu bar height...
            JMenuBar jmb = getSwingComponent().getJMenuBar();
            if (jmb != null) {
                insets.top += jmb.getPreferredSize().height;
            }
        }
        
        return insets;
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
    public void repositionSecurityWarning() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateWindow() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void peerPaint(Graphics g, boolean update) {

        /*
         * Don't paint the whole area, we only need to paint the frame
         * borders
         */
        JComponent c = getSwingComponent();
        if (c != null) {
            Insets insets = getInsets();

            Area clip = new Area(c.getBounds());
            Rectangle clip2 =
                    new Rectangle(insets.left,
                                  insets.top,
                                  c.getWidth() - insets.left - insets.right,
                                  c.getHeight() - insets.bottom - insets.top);
            clip.subtract(new Area(clip2));

            Graphics peerG = new WindowClippedGraphics((Graphics2D) g, clip);
            try {
                c.paint(peerG);
            } finally {
                peerG.dispose();
            }
        }
    }
}
