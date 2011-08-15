package net.java.openjdk.awt.peer.web;

import java.awt.*;
import java.util.concurrent.locks.*;
import javax.swing.*;

import net.java.openjdk.cacio.servlet.*;
import net.java.openjdk.cacio.servlet.transport.*;
import sun.awt.*;
import sun.awt.peer.cacio.*;

public class WebSessionState {
    ReentrantLock sessionLock = new ReentrantLock();
    WebMouseStateTracker mouseTracker;
    WebKeyboardStateTracker keyboardTracker;
    WebGraphicsConfiguration config;
    int subSessionID;
    
    String[] cmdLineParams;
    String mainClsName;
    Dimension initialScreenDimension;
//    String transportFormat;
    Transport backend;
    
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

}
