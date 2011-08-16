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

package net.java.openjdk.cacio.servlet;

import java.awt.KeyboardFocusManager;
import java.lang.reflect.*;
import java.util.*;

import net.java.openjdk.awt.peer.web.*;
import sun.awt.*;

/**
 * Initializes a new AppContext, and starts the specified Application within the
 * new AppContext. Waits until the Application has started up, to avoid browsers
 * fetching image-data and pumping events, before initialization is finished.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class AppContextCreator {

    protected void startAppInNewAppContext(final WebSessionState sessionState) {

	ThreadGroup appGroup = new ThreadGroup("AppThreadGroup " + String.valueOf(new Random().nextLong()));
	Thread t = new Thread(appGroup, "AppInitThread") {
	    public void run() {
		AppContext appContext = SunToolkit.createNewAppContext();
		KeyboardFocusManager.setCurrentKeyboardFocusManager(new WebKeyboardFocusManager());
		try {
		    sessionState.lockSession();
		    WebSessionManager.getInstance().registerAppContext(appContext, sessionState);

		    ClassLoader loader = getClass().getClassLoader();
		    Class<?> cls = loader.loadClass(sessionState.getMainClsName());
		    Method mainMethod = cls.getMethod("main", String[].class);
		    mainMethod.setAccessible(true);
		    mainMethod.invoke(cls, (Object) sessionState.getCmdLineParams());
		} catch (Exception ex) {
		    ex.printStackTrace();
		} finally {
		    sessionState.unlockSession();
		}
	    }
	};

	try {
	    t.start();

	    // Wait for initialization to finish
	    t.join();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }
}
