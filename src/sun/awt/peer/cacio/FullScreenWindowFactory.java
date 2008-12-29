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

public class FullScreenWindowFactory implements PlatformWindowFactory {

    /**
     * The container for the toplevel windows.
     */
    private ManagedWindowContainer screen;

    /**
     * Constructs a new FullScreenWindowFactory that uses the
     * specified container as container for all toplevel windows.
     *
     * @param screen the container to be used for toplevel windows
     */
    public FullScreenWindowFactory(ManagedWindowContainer screen) {
        this.screen = screen;
    }

    /**
     * Creates a {@link PlatformWindow} instance.
     *
     * @param cacioComponent the corresponding Cacio component
     * @parent the parent window, or <code>null</code> for top level windows
     *
     * @return the platform window instance
     */
    public PlatformWindow createPlatformWindow(CacioComponent awtComponent,
                                               PlatformWindow parent) {
        ManagedWindow window;
        if (parent != null) {
            ManagedWindow p = (ManagedWindow) parent;
            window = new ManagedWindow(p, awtComponent);
        } else {
            window = new ManagedWindow(screen, awtComponent);
        }
        return window;
    }

    @Override
    public CacioEventSource createEventSource() {
        // TODO: Implement fullscreen translating event source.
        return null;
    }
}
