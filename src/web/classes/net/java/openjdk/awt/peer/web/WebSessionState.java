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
import java.util.concurrent.locks.*;
import net.java.openjdk.cacio.servlet.transport.*;
import sun.awt.*;

/**
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class WebSessionState {
    ReentrantLock sessionLock = new ReentrantLock();
    WebMouseStateTracker mouseTracker;
    WebKeyboardStateTracker keyboardTracker;
    WebGraphicsConfiguration config;
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
    
    public void lockSession() {
	sessionLock.lock();
    }

    public void unlockSession() {
	sessionLock.unlock();
    }

    public WebGraphicsConfiguration getGraphicsConfiguration() {
	return config;
    }

    public void setGraphicsConfiguration(WebGraphicsConfiguration config) {
	this.config = config;
	mouseTracker = new WebMouseStateTracker(config.getScreen());
	keyboardTracker = new WebKeyboardStateTracker(config.getScreen());
    }

    public WebMouseStateTracker getMouseTracker() {
	return mouseTracker;
    }

    public WebKeyboardStateTracker getKeyboardTracker() {
	return keyboardTracker;
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
    
    public void dispose()  {
	appContext.dispose();
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

//    public String getTransportFormat() {
//        return transportFormat;
//    }
//
//    public void setTransportFormat(String transportFormat) {
//        this.transportFormat = transportFormat;
//    }

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
}
