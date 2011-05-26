package net.java.openjdk.awt.peer.web;

import java.util.*;
import java.util.concurrent.locks.*;

import javax.servlet.http.*;

public class WebSessionState {
    static final String SESSION_KEY = "SDLSessionState";
    static ThreadLocal<WebSessionState> stateStore = new ThreadLocal<WebSessionState>();

    ReentrantLock sessionLock = new ReentrantLock();
    WebMouseStateTracker mouseTracker;
    WebKeyboardStateTracker keyboardTracker;
    WebGraphicsConfiguration config;
    
    public void lockSession() {
	sessionLock.lock();
    }
    
    public void unlockSession() {
	sessionLock.unlock();
    }
    
    public static Integer register(HttpSession session, Integer subsessionID) {
	WebSessionState sessionState = null;

	synchronized (stateStore) {
	    List<WebSessionState> subSessionList = (List<WebSessionState>) session.getAttribute(SESSION_KEY);
	    if(subSessionList == null) {
		subSessionList = new ArrayList<WebSessionState>();
		session.setAttribute(SESSION_KEY, subSessionList);
	    }
	    
	    if(subsessionID == null) {
		subsessionID = subSessionList.size();
	    } else {
		sessionState = subSessionList.get(subsessionID);
	    }
	    
	    if (sessionState == null) {
		sessionState = new WebSessionState();
		subSessionList.add(sessionState);
	    }

	    stateStore.set(sessionState);
	}
	
	return subsessionID;
    }

    public static void unregister() {
	synchronized (stateStore) {
	    WebSessionState state = stateStore.get();
	    stateStore.set(null);
	}
    }

    public static WebSessionState getCurrentState() {
	return (WebSessionState) stateStore.get();
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
}
