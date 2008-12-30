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

import java.awt.Component;
import java.awt.Insets;
import java.awt.peer.ContainerPeer;

public abstract class CacioContainerPeer extends CacioComponentPeer implements
        ContainerPeer {

    public CacioContainerPeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }

    public void beginLayout() {

        // This can be used for optimization (e.g. defer painting while
        // layouting). Nothing to do here for now.

    }

    public void endLayout() {

        // This can be used for optimization (e.g. defer painting while
        // layouting). Nothing to do here for now.

    }

    public void beginValidate() {

        // This can be used for optimization (e.g. defer painting while
        // layouting). Nothing to do here for now.

    }

    public void endValidate() {

        // This can be used for optimization (e.g. defer painting while
        // layouting). Nothing to do here for now.

    }

    public Insets getInsets() {

        return platformWindow.getInsets();

    }

    public boolean isRestackSupported() {

        return platformWindow.isRestackSupported();

    }

    public void restack() {

        platformWindow.restack();

    }

}
