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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.WindowPeer;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.border.Border;

import sun.awt.ComponentAccessor;

class CacioWindowPeer extends CacioContainerPeer implements WindowPeer {

    private static boolean decorateWindows = "true".equals(System.getProperty("cacio.decorateWindows", "true"));

    class SwingRootPane extends JRootPane implements CacioSwingComponent {

        private Window window;

        SwingRootPane(Window w) {
            window = w;
            ComponentAccessor.setParent(this, w);
        }

        /**
         * Overridden so that this method returns the correct value even without
         * a peer.
         * 
         * @return the screen location of the button
         */
        @Override
        public Point getLocationOnScreen() {
            return CacioWindowPeer.this.getLocationOnScreen();
        }

        /**
         * Overridden so that the isShowing method returns the correct value for
         * the swing button, even if it has no peer on its own.
         * 
         * @return <code>true</code> if the button is currently showing,
         *         <code>false</code> otherwise
         */
        @Override
        public boolean isShowing() {
            boolean retVal = false;
            if (window != null)
                retVal = window.isShowing();
            return retVal;
        }

        /**
         * Overridden, so that the Swing button can create an Image without its
         * own peer.
         * 
         * @param w
         *            the width of the image
         * @param h
         *            the height of the image
         * 
         * @return an image
         */
        @Override
        public Image createImage(int w, int h) {
            return CacioWindowPeer.this.createImage(w, h);
        }

        /**
         * Overridden, so that the Swing button can create a Graphics without
         * its own peer.
         * 
         * @return a graphics instance for the button
         */
        @Override
        public Graphics getGraphics() {
            return CacioWindowPeer.this.getGraphics();
        }

        /**
         * Returns this button.
         * 
         * @return this button
         */
        @Override
        public JComponent getJComponent() {
            return this;
        }

        private MouseEvent retargetMouseEvent(MouseEvent ev, Component c) {
            // Translate to target coordinates.
            int x = ev.getX();
            int y = ev.getY();
            Component p = c;
            while (p != this) {
                x -= p.getX();
                y -= p.getY();
                p = p.getParent();
            }
            ev = new MouseEvent(c, ev.getID(), ev.getWhen(), ev.getModifiers(),
                                x, y, ev.getClickCount(), ev.isPopupTrigger(),
                                ev.getButton());
            return ev;
        }

        /**
         * Handles mouse events by forwarding it to
         * <code>processMouseEvent()</code> after having retargetted it to
         * this button.
         * 
         * @param ev
         *            the mouse event
         */
        @Override
        public void handleMouseEvent(MouseEvent ev) {
            Component c = findComponentAt(ev.getX(), ev.getY());
            if (c != null) {
                ev = retargetMouseEvent(ev, c);
                processMouseEvent(c, ev);
            }
        }

        /**
         * Handles mouse motion events by forwarding it to
         * <code>processMouseMotionEvent()</code> after having retargetted it
         * to this button.
         * 
         * @param ev
         *            the mouse motion event
         */
        @Override
        public void handleMouseMotionEvent(MouseEvent ev) {
            Component c = findComponentAt(ev.getX(), ev.getY());
            if (c != null) {
                ev = retargetMouseEvent(ev, c);
                processMouseMotionEvent(c, ev);
            }
        }

        void processMouseEvent(Component c, MouseEvent ev) {
            ComponentAccessor.processEvent(c, ev);
        }

        void processMouseMotionEvent(Component c, MouseEvent ev) {
            ComponentAccessor.processEvent(c, ev);
        }

        /**
         * Handles key events by forwarding it to <code>processKeyEvent()</code>
         * after having retargetted it to this button.
         * 
         * @param ev the mouse event
         */
        @Override
        public void handleKeyEvent(KeyEvent ev) {
        }

        @Override
        public void handleFocusEvent(FocusEvent ev) {
        }

    }

    CacioWindowPeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
        ((Window) awtC).setFocusableWindowState(true);
        ((Window) awtC).setFocusTraversalPolicyProvider(true);
    }

    void init(PlatformWindowFactory pwf) {
        
        platformWindow = pwf.createPlatformToplevelWindow(this);

        initSwingComponent();
    }

    @Override
    void initSwingComponent() {
        if (decorateWindows) {
            Window window = (Window) getAWTComponent();
            SwingRootPane rootPane = new SwingRootPane(window);
            int deco = getRootPaneDecorationStyle();
            rootPane.setWindowDecorationStyle(deco);
            setSwingComponent(rootPane);
            rootPane.addNotify();
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

    private JRootPane getRootPane() {
        CacioSwingComponent swingComp = getSwingComponent();
        if (swingComp != null) {
            return (JRootPane) swingComp.getJComponent();
        } else {
            return null;
        }
    }

    public Insets getInsets() {

        JRootPane rp = getRootPane();
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
    }
}
