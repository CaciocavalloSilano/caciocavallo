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

package sun.awt.peer.x11;

import java.awt.Composite;
import sun.awt.SunToolkit;
import sun.awt.peer.cacio.BlitClipHelper;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

class X11Blit extends Blit implements BlitClipHelper.Blitter {

    static void register() {
        GraphicsPrimitive[] prims = {

            new X11Blit(X11SurfaceData.typeDefault, X11SurfaceData.typeDefault, false),
        };
        GraphicsPrimitiveMgr.register(prims);
    }

    private native void nativeBlit(long display,
                                   SurfaceData src, SurfaceData dst,
                                   int sx, int sy, int dx, int dy,
                                   int w, int h);

    X11Blit(SurfaceType srcType, SurfaceType dstType, boolean over) {
        super(srcType,
              over ? CompositeType.SrcOverNoEa : CompositeType.SrcNoEa,
              dstType);
    }

    @Override
    public void Blit(SurfaceData src, SurfaceData dst, Composite comp,
                     Region clip, int sx, int sy, int dx, int dy,
                     int w, int h) {
        SunToolkit.awtLock();
        try {
            BlitClipHelper.blitWithAnyClip(this, src, dst, comp, clip, sx, sy,
                                           dx, dy, w, h);
        } finally {
            SunToolkit.awtUnlock();
        }
    }

    public void doBlit(SurfaceData src, SurfaceData dst, Composite comp,
                       int sx, int sy, int dx, int dy, int w, int h) {
        nativeBlit(X11GraphicsEnvironment.getDisplay(), src, dst,
                   sx, sy, dx, dy, w, h);
    }
}
