package net.java.openjdk.awt.peer.web;

import java.util.concurrent.locks.*;

public class WebSessionState {
    ReentrantLock sessionLock = new ReentrantLock();
    WebMouseStateTracker mouseTracker;
    WebKeyboardStateTracker keyboardTracker;
    WebGraphicsConfiguration config;
    int subSessionID;

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
}
