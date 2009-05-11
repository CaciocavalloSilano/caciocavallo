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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import sun.awt.FontConfiguration;
import sun.font.FcFontConfiguration;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceManagerFactory;

public class X11GraphicsEnvironment extends SunGraphicsEnvironment {

    private long display;

    private native long initDisplay();

    public X11GraphicsEnvironment() {
        System.loadLibrary("cacio-x11");
        display = initDisplay();
        SurfaceManagerFactory.setInstance(new X11SurfaceManagerFactory());
    }

    @Override
    protected int getNumScreens() {
        // TODO: Maybe add multiscreen support.
        return 1;
    }

    @Override
    protected GraphicsDevice makeScreenDevice(int arg0) {
        return new X11GraphicsDevice();
    }

    @Override
    protected FontConfiguration createFontConfiguration() {
        return new FcFontConfiguration(this);
    }

    @Override
    public FontConfiguration createFontConfiguration(boolean preferLocale,
                                                     boolean preferProp) {
        return new FcFontConfiguration(this, preferLocale, preferProp);
    }

    @Override
    public boolean isDisplayLocal() {
        // TODO: Implement.
        return true;
    }

    static long getDisplay() {
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        X11GraphicsEnvironment xge = (X11GraphicsEnvironment) ge;
        return xge.display;
    }
}
