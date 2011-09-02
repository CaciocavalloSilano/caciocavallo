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

import java.awt.event.*;
import java.lang.reflect.*;
import java.util.concurrent.locks.*;
import java.util.logging.*;

import net.java.openjdk.cacio.servlet.transport.*;
import sun.awt.*;

/**
 * WebSessionState holds, as its name implies, session-related state. It
 * provides access to the AWT "session" represented by AppContexts, from the
 * servlet session.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class WebSessionState {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    ReentrantLock sessionLock = new ReentrantLock();
    volatile WebEventManager eventManager;
    volatile WebGraphicsConfiguration config;
    volatile WebScreen screen;
    WebWindowFactory windowFactory;
    WebFocusManager focusManager;
    int subSessionID;

    String[] cmdLineParams;
    String mainClsName;
    Dimension initialScreenDimension;
    Transport backend;
    int compressLevel;

    AppContext appContext;

    public WebSessionState(int subSessionID) {
	this.subSessionID = subSessionID;
    }

    /**
     * Lock the session lock
     */
    public void lockSession() {
	sessionLock.lock();
    }

    /**
     * Unlock the session lock
     */
    public void unlockSession() {
	sessionLock.unlock();
    }

    public WebGraphicsConfiguration getGraphicsConfiguration() {
	return config;
    }

    /**
     * Set the GraphicsConfiguration, and initialize InputState trackers
     * 
     * @param config
     */
    public void setGraphicsConfiguration(WebGraphicsConfiguration config) {
	this.config = config;
	eventManager = new WebEventManager(config.getScreen());
    }


    public int getSubSessionID() {
	return subSessionID;
    }

    public AppContext getAppContext() {
	return appContext;
    }

    public void setAppContext(AppContext appContext) {
	this.appContext = appContext;
    }

    public void dispose() {
	if (appContext != null) {
	    logSessionDispose();
	    
	    SunToolkit.postEvent(appContext, new InvocationEvent(this, new Runnable() {
		public void run() {
		    Window[] windowArray = Window.getWindows();
		    for(Window w : windowArray) {
			w.dispose();
		    }
		}
	    }));
	    
	    appContext.dispose();
	}
	screen = null;
	windowFactory = null;
	focusManager = null;
    }

    private void logSessionDispose() {
	EventQueue queue = (EventQueue) appContext.get(AppContext.EVENT_QUEUE_KEY);
	if (queue != null) {
	    try {
		Method getDispatchThreadMethod = queue.getClass().getDeclaredMethod("getDispatchThread", new Class[0]);
		getDispatchThreadMethod.setAccessible(true);
		Thread edt = (Thread) getDispatchThreadMethod.invoke(queue, new Object[0]);
		if(edt != null) {
		    logger.log(Level.INFO, "Shutting down: " + edt.getName());
		}
	    } catch (ReflectiveOperationException ex) {
		logger.log(Level.WARNING, "Unable to reflectivly accessing the EventQueue's Dispatch Thread", ex);
	    }
	}
    }

    public String[] getCmdLineParams() {
	return cmdLineParams;
    }

    public void setCmdLineParams(String[] cmdLineParams) {
	this.cmdLineParams = cmdLineParams;
    }

    public String getMainClsName() {
	return mainClsName;
    }

    public void setMainClsName(String mainClsName) {
	this.mainClsName = mainClsName;
    }

    public Dimension getInitialScreenDimension() {
	return initialScreenDimension;
    }

    public void setInitialScreenDimension(Dimension initialScreenDimension) {
	this.initialScreenDimension = initialScreenDimension;
    }

    public Transport getBackend() {
	return backend;
    }

    public void setBackend(Transport backend) {
	this.backend = backend;
    }

    public int getCompressLevel() {
	return compressLevel;
    }

    public void setCompressLevel(int compressLevel) {
	this.compressLevel = compressLevel;
    }

    public WebScreen getScreen() {
	return screen;
    }

    public void setScreen(WebScreen screen) {
	this.screen = screen;
    }

    public WebWindowFactory getWindowFactory() {
	return windowFactory;
    }

    public void setWindowFactory(WebWindowFactory windowFactory) {
	this.windowFactory = windowFactory;
    }

    public WebFocusManager getFocusManager() {
	return focusManager;
    }

    public void setFocusManager(WebFocusManager focusManager) {
	this.focusManager = focusManager;
    }

    public WebEventManager getEventManager() {
        return eventManager;
    }
}
