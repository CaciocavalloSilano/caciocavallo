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
package net.java.openjdk.awt.peer.web;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

import net.java.openjdk.awt.peer.sdl.*;

/**
 * @author Mario Torre <neugens.limasoftware@gmail.com>
 */
class WebGraphicsDevice extends GraphicsDevice {

    // private SDLGraphicsConfiguration defaultConfig;

    @Override
    public int getType() {
	return GraphicsDevice.TYPE_RASTER_SCREEN;
    }

    @Override
    public String getIDstring() {
	return "SDL Cacio Device";
    }

    @Override
    public GraphicsConfiguration[] getConfigurations() {
	return new GraphicsConfiguration[] { getDefaultConfiguration() };
    }

    @Override
    public synchronized GraphicsConfiguration getDefaultConfiguration() {
	WebSessionState state = WebSessionState.getCurrentState();
	if (state != null) {
	    if (state.getGraphicsConfiguration() == null) {
		state.setGraphicsConfiguration(new WebGraphicsConfiguration(this));
	    }
	    return state.getGraphicsConfiguration();
	} else {
	    //provide Dummy GraphicsConfiguration for repaint-manager
	    return new WebGraphicsConfiguration();
	}
    }

}
