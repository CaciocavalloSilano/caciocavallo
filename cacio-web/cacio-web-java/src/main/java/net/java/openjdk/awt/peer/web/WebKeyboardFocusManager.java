/*
 * Copyright (c) 2011, Clemens Eisserer, Oracle and/or its affiliates. All rights reserved.
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

package net.java.openjdk.awt.peer.web;

import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.beans.PropertyVetoException;

import sun.util.logging.PlatformLogger;

public class WebKeyboardFocusManager extends DefaultKeyboardFocusManager {

    private static final PlatformLogger focusLog = PlatformLogger.getLogger("net.java.openjdk.awt.peer.web.WebKeyboardFocusManager");
    private static final String notPrivileged = "this KeyboardFocusManager is not installed in the current thread's context";

    private Component focusOwner;
    private Component permanentFocusOwner;
    private Window focusedWindow;
    private Window activeWindow;
    
    public Component getFocusOwner() {
        synchronized (KeyboardFocusManager.class) {
            if (focusOwner == null) {
                return null;
            }

            return focusOwner;
        }
    }

    protected Component getGlobalFocusOwner() throws SecurityException {
        synchronized (KeyboardFocusManager.class) {
            if (this == getCurrentKeyboardFocusManager()) {
                return focusOwner;
            } else {
                if (focusLog.isLoggable(PlatformLogger.FINER)) {
                    focusLog.finer("This manager is " + this + ", current is " + getCurrentKeyboardFocusManager());
                }
                throw new SecurityException(notPrivileged);
            }
        }
    }

    protected void setGlobalFocusOwner(Component focusOwner) {
        Component oldFocusOwner = null;
        boolean shouldFire = false;

        if (focusOwner == null || focusOwner.isFocusable()) {
            synchronized (KeyboardFocusManager.class) {
                oldFocusOwner = getFocusOwner();

                try {
                    fireVetoableChange("focusOwner", oldFocusOwner,
                                       focusOwner);
                } catch (PropertyVetoException e) {
                    // rejected
                    return;
                }

                this.focusOwner = focusOwner;

                if (focusOwner != null &&
                    (getCurrentFocusCycleRoot() == null ||
                     !focusOwner.isFocusCycleRoot(getCurrentFocusCycleRoot())))
                {
                    Container rootAncestor =
                        focusOwner.getFocusCycleRootAncestor();
                    if (rootAncestor == null && (focusOwner instanceof Window))
                    {
                        rootAncestor = (Container)focusOwner;
                    }
                    if (rootAncestor != null) {
                        setGlobalCurrentFocusCycleRoot(rootAncestor);
                    }
                }

                shouldFire = true;
            }
        }

        if (shouldFire) {
            firePropertyChange("focusOwner", oldFocusOwner, focusOwner);
        }
    }

    protected Component getGlobalPermanentFocusOwner()
            throws SecurityException
        {
            synchronized (KeyboardFocusManager.class) {
                if (this == getCurrentKeyboardFocusManager()) {
                    return permanentFocusOwner;
                } else {
                    if (focusLog.isLoggable(PlatformLogger.FINER)) {
                        focusLog.finer("This manager is " + this + ", current is " + getCurrentKeyboardFocusManager());
                    }
                    throw new SecurityException(notPrivileged);
                }
            }
        }

    public Component getPermanentFocusOwner() {
        synchronized (KeyboardFocusManager.class) {
            if (permanentFocusOwner == null) {
                return null;
            }

            return permanentFocusOwner;
        }
    }

    protected void setGlobalPermanentFocusOwner(Component permanentFocusOwner)
    {
        Component oldPermanentFocusOwner = null;
        boolean shouldFire = false;

        if (permanentFocusOwner == null || permanentFocusOwner.isFocusable()) {
            synchronized (KeyboardFocusManager.class) {
                oldPermanentFocusOwner = getPermanentFocusOwner();

                try {
                    fireVetoableChange("permanentFocusOwner",
                                       oldPermanentFocusOwner,
                                       permanentFocusOwner);
                } catch (PropertyVetoException e) {
                    // rejected
                    return;
                }

                this.permanentFocusOwner = permanentFocusOwner;

                // TODO: If this turns out to be important we need to call it via reflection.
//                KeyboardFocusManager.
//                    setMostRecentFocusOwner(permanentFocusOwner);

                shouldFire = true;
            }
        }

        if (shouldFire) {
            firePropertyChange("permanentFocusOwner", oldPermanentFocusOwner,
                               permanentFocusOwner);
        }
    }
    public Window getFocusedWindow() {
        synchronized (KeyboardFocusManager.class) {
            if (focusedWindow == null) {
                return null;
            }

            return focusedWindow;
        }
    }

    protected Window getGlobalFocusedWindow() throws SecurityException {
        synchronized (KeyboardFocusManager.class) {
            if (this == getCurrentKeyboardFocusManager()) {
               return focusedWindow;
            } else {
                if (focusLog.isLoggable(PlatformLogger.FINER)) {
                    focusLog.finer("This manager is " + this + ", current is " + getCurrentKeyboardFocusManager());
                }
                throw new SecurityException(notPrivileged);
            }
        }
    }

    protected void setGlobalFocusedWindow(Window focusedWindow) {
        Window oldFocusedWindow = null;
        boolean shouldFire = false;

        if (focusedWindow == null || focusedWindow.isFocusableWindow()) {
            synchronized (KeyboardFocusManager.class) {
                oldFocusedWindow = getFocusedWindow();

                try {
                    fireVetoableChange("focusedWindow", oldFocusedWindow,
                                       focusedWindow);
                } catch (PropertyVetoException e) {
                    // rejected
                    return;
                }

                this.focusedWindow = focusedWindow;
                shouldFire = true;
            }
        }

        if (shouldFire) {
            firePropertyChange("focusedWindow", oldFocusedWindow,
                               focusedWindow);
        }
    }

    public Window getActiveWindow() {
        synchronized (KeyboardFocusManager.class) {
            if (activeWindow == null) {
                return null;
            }

            return activeWindow;
        }
    }

    /**
     * Returns the active Window, even if the calling thread is in a different
     * context than the active Window. Only a Frame or a Dialog can be the
     * active Window. The native windowing system may denote the active Window
     * or its children with special decorations, such as a highlighted title
     * bar. The active Window is always either the focused Window, or the first
     * Frame or Dialog that is an owner of the focused Window.
     * <p>
     * This method will throw a SecurityException if this KeyboardFocusManager
     * is not the current KeyboardFocusManager for the calling thread's
     * context.
     *
     * @return the active Window
     * @see #getActiveWindow
     * @see #setGlobalActiveWindow
     * @throws SecurityException if this KeyboardFocusManager is not the
     *         current KeyboardFocusManager for the calling thread's context
     */
    protected Window getGlobalActiveWindow() throws SecurityException {
        synchronized (KeyboardFocusManager.class) {
            if (this == getCurrentKeyboardFocusManager()) {
               return activeWindow;
            } else {
                if (focusLog.isLoggable(PlatformLogger.FINER)) {
                    focusLog.finer("This manager is " + this + ", current is " + getCurrentKeyboardFocusManager());
                }
                throw new SecurityException(notPrivileged);
            }
        }
    }

    /**
     * Sets the active Window. Only a Frame or a Dialog can be the active
     * Window. The native windowing system may denote the active Window or its
     * children with special decorations, such as a highlighted title bar. The
     * active Window is always either the focused Window, or the first Frame or
     * Dialog that is an owner of the focused Window.
     * <p>
     * This method does not actually change the active Window as far as the
     * native windowing system is concerned. It merely stores the value to be
     * subsequently returned by <code>getActiveWindow()</code>. Use
     * <code>Component.requestFocus()</code> or
     * <code>Component.requestFocusInWindow()</code>to change the active
     * Window, subject to platform limitations.
     *
     * @param activeWindow the active Window
     * @see #getActiveWindow
     * @see #getGlobalActiveWindow
     * @see Component#requestFocus()
     * @see Component#requestFocusInWindow()
     * @beaninfo
     *       bound: true
     */
    protected void setGlobalActiveWindow(Window activeWindow) {
        Window oldActiveWindow;
        synchronized (KeyboardFocusManager.class) {
            oldActiveWindow = getActiveWindow();
            if (focusLog.isLoggable(PlatformLogger.FINER)) {
                focusLog.finer("Setting global active window to " + activeWindow + ", old active " + oldActiveWindow);
            }

            try {
                fireVetoableChange("activeWindow", oldActiveWindow,
                                   activeWindow);
            } catch (PropertyVetoException e) {
                // rejected
                return;
            }

            this.activeWindow = activeWindow;
        }

        firePropertyChange("activeWindow", oldActiveWindow, activeWindow);
    }
}
