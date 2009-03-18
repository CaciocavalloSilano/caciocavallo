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

package sun.awt.peer.cacio;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.peer.ScrollPanePeer;

class CacioScrollPanePeer extends CacioContainerPeer
                          implements ScrollPanePeer {


    CacioScrollPanePeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }

    @Override
    public int getHScrollbarHeight() {
        System.err.println("IMPLEMENT ME: CacioScrollPanePeer.getHScrollbarHeight()");
        return 0;
    }

    @Override
    public int getVScrollbarWidth() {
        System.err.println("IMPLEMENT ME: CacioScrollPanePeer.getVScrollbarWidth()");
        return 0;
    }

    @Override
    public void setScrollPosition(int arg0, int arg1) {
        System.err.println("IMPLEMENT ME: CacioScrollPanePeer.setScrollPosition()");
    }

    @Override
    public void childResized(int arg0, int arg1) {
        System.err.println("IMPLEMENT ME: CacioScrollPanePeer.childResized()");
    }

    @Override
    public void setUnitIncrement(Adjustable arg0, int arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(Adjustable arg0, int arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
