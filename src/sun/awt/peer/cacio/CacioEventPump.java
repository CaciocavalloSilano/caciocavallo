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
import java.awt.peer.ComponentPeer;

import sun.awt.AppContext;
import sun.awt.AWTAutoShutdown;
import sun.awt.SunToolkit;

/**
 * A thread that polls the native event queue for events and posts
 * them to the AWT event queue.
 */
class CacioEventPump implements Runnable {

    private CacioEventSource source;

    /**
     * Creates and starts a CacioEventPump with the specified
     * event source.
     *
     * @param s the event source to get events from
     */
    CacioEventPump(CacioEventSource s) {
        source = s;
        Thread t = new Thread(this, "CacioEventPump");
        t.setDaemon(true);
        t.start();
    }

    /**
     * The main loop of the event pump.
     */
    public void run() {

        AWTAutoShutdown.notifyToolkitThreadBusy();
        while (true) {
            // Jump out if we get interrupted.
            if (Thread.interrupted()) {
                return;
            }
            try {
                AWTAutoShutdown.notifyToolkitThreadFree();
                EventData ev = source.getNextEvent();
                AWTAutoShutdown.notifyToolkitThreadBusy();
                if (ev != null) {
                    Object source = ev.getSource();
                    if (source != null && ev.getId() != 0) {
                        if (source instanceof ManagedWindowContainer) {
                            ManagedWindowContainer c =
                                (ManagedWindowContainer) source;
                            c.dispatchEvent(ev);
                        } else if (source instanceof CacioComponent) {
                            CacioComponent c = (CacioComponent) source;
                            ev.setSource(c.getAWTComponent());
                            c.handlePeerEvent(ev.createAWTEvent());
                        } else if (source instanceof Component) {
                            AWTEvent awtEvent = ev.createAWTEvent();
                            if (awtEvent != null) {
                                SunToolkit.postEvent(AppContext.getAppContext(),
                                        ev.createAWTEvent());
                            }

                        }
                    }
                }
            } catch (Exception ex) {
                // Print stack trace but don't kill the pump.
                ex.printStackTrace();
            }
        }
    }
}