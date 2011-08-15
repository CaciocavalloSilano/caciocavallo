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

import java.awt.*;
import sun.awt.peer.cacio.*;
import sun.awt.peer.cacio.managed.*;

/**
 * FullScreenWindowFactory for Caciocavallo-Web.
 * 
 * This differs from the "usual" FullScreenWindowFactory implementations, by
 * tricking Caciocavallo to not start an event-pump-thread. As Caciocavallo-Web
 * gets events pushed by a servlet-thread, instead of pulling it from a possibly
 * blocking event source, there is no need for a seperate thread.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class WebWindowFactory extends FullScreenWindowFactory {

    public WebWindowFactory() {
	super(new SessionScreenSelector(), null);
    }

    @Override
    public CacioEventPump<?> createEventPump() {
	return new WebDummyEventPump();
    }

    public void repaintScreen(PlatformScreen screen) {
	ScreenManagedWindowContainer smwc = getScreenManagedWindowContainer(screen);
	smwc.repaint(0, 0, screen.getBounds().width, screen.getBounds().height);
    }

    private static final class SessionScreenSelector implements PlatformScreenSelector {
	@Override
	public PlatformScreen getPlatformScreen(GraphicsConfiguration config) {
	    return ((WebGraphicsConfiguration) config).getScreen();
	}
    }
}

/**
 * Dummy EventPump.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
class WebDummyEventPump extends CacioEventPump<EventData> {
    /**
     * Do not start an event-pump thread at all, by overriding Thread.start()
     * with a no-op method
     */
    @Override
    protected void start() {
    }

    @Override
    protected EventData fetchNativeEvent() {
	return null;
    }

    @Override
    protected void dispatchNativeEvent(EventData nativeEvent) {
    }
}