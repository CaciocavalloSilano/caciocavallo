package net.java.openjdk.awt.peer.web;

import java.util.*;
import java.util.concurrent.locks.*;

import javax.servlet.http.*;

import sun.awt.*;

public class WebSessionState {
    static final String SESSION_KEY = "WEBSessionState";

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
    
    public static Integer getSubSessionID(HttpSession session) {
	List<WebSessionState> subSessionList = (List<WebSessionState>) session.getAttribute(SESSION_KEY);
	if(subSessionList == null) {
	    return 0;
	} else {
	    synchronized(subSessionList) {
		return subSessionList.size() - 1;
	    }
	}
    }

    public static Integer register(HttpSession session, Integer subsessionID) {
	WebSessionState sessionState = null;
	List<WebSessionState> subSessionList = (List<WebSessionState>) session.getAttribute(SESSION_KEY);

	if(subSessionList == null) {
	    subSessionList = new ArrayList<WebSessionState>();
	    session.setAttribute(SESSION_KEY, subSessionList);
	}
	
	synchronized (subSessionList) {
	    if (subSessionList == null) {
		subSessionList = new ArrayList<WebSessionState>();
		session.setAttribute(SESSION_KEY, subSessionList);
	    }

	    if (subsessionID == null) {
		subsessionID = subSessionList.size();
	    } else {
		sessionState = subSessionList.get(subsessionID);
	    }

	    if (sessionState == null) {
		sessionState = new WebSessionState();
		subSessionList.add(sessionState);
		AppContext.getAppContext().put(SESSION_KEY, sessionState);
	    }
	}

	return subsessionID;
    }

    public static void unregister() {
	// synchronized (stateStore) {
	// WebSessionState state = stateStore.get();
	// stateStore.set(null);
	// }
    }

    public static WebSessionState getCurrentStateAWT() {
	return (WebSessionState) AppContext.getAppContext().get(SESSION_KEY);
    }
    
    public static WebSessionState getCurrentState(HttpSession session, int subSessionID) {
	List<WebSessionState> subSessionList = (List<WebSessionState>) session.getAttribute(SESSION_KEY);
	if(subSessionList != null) {
	    synchronized(subSessionList) {
		return subSessionList.get(subSessionID);
	    }
	}
	
	return null;
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
