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
package net.java.openjdk.awt.peer.sdl;

import java.awt.GraphicsDevice;

import sun.awt.FontConfiguration;
import sun.font.FcFontConfiguration;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceManagerFactory;

/**
 * Graphics Environment implementation based on libSDL.
 *
 * @author Mario Torre <neugens.liamsoftware@gmail.com>
 */
public class SDLGraphicsEnvironment extends SunGraphicsEnvironment {

    static {
        System.loadLibrary("cacio-sdl");
        boolean initialised = nativeInit();
        if (!initialised) {
            throw new ExceptionInInitializerError("Cannot initiliase libSDL!");
        }
        SurfaceManagerFactory.setInstance(new SDLSurfaceManagerFactory());
    }

    @Override
    protected int getNumScreens() {

        return 1;
    }

    @Override
    protected GraphicsDevice makeScreenDevice(int screennum) {

        return new SDLGraphicsDevice();
    }

    @Override
    public boolean isDisplayLocal() {

        return true;
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

    private static final native boolean nativeInit();
}
