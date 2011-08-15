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

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

import net.java.openjdk.cacio.servlet.*;

/**
 * @author Mario Torre <neugens.limasoftware@gmail.com>
 */
class WebGraphicsDevice extends GraphicsDevice {

    @Override
    public int getType() {
	return GraphicsDevice.TYPE_RASTER_SCREEN;
    }

    @Override
    public String getIDstring() {
	return "Web Cacio Device";
    }

    @Override
    public GraphicsConfiguration[] getConfigurations() {
	return new GraphicsConfiguration[] { getDefaultConfiguration() };
    }

    @Override
    public synchronized GraphicsConfiguration getDefaultConfiguration() {
	WebSessionState state = WebSessionManager.getInstance().getCurrentStateAWT();

	if (state == null) {
	    //Create a dummy WebGraphicsConfiguration for 
	    //the strike cache disposer, which is not in our threadgroup.
	    return new WebGraphicsConfiguration();
	}

	if (state.getGraphicsConfiguration() == null) {
	    state.setGraphicsConfiguration(new WebGraphicsConfiguration(this));
	}
	return state.getGraphicsConfiguration();
    }

}
