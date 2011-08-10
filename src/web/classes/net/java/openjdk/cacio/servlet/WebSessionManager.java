package net.java.openjdk.cacio.servlet;

import java.util.*;

import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;
import sun.awt.*;

public class WebSessionManager {
    static final String SESSION_KEY = "WEBSessionState";

    private static final WebSessionManager instance = new WebSessionManager();
    
    public static WebSessionManager getInstance() {
	return instance;
    }
    
    public synchronized WebSessionState register(HttpSession session) {
	List<WebSessionState> subSessionList = (List<WebSessionState>) session.getAttribute(SESSION_KEY);

	if (subSessionList == null) {
	    subSessionList = new ArrayList<WebSessionState>();
	    session.setAttribute(SESSION_KEY, subSessionList);
	}

	int subSessionID = subSessionList.size();
	WebSessionState sessionState = new WebSessionState(subSessionID);
	subSessionList.add(sessionState);
	
	return sessionState;
    }
    
    public void registerAppContext(AppContext context, WebSessionState state) {
	state.setAppContext(context);
	AppContext.getAppContext().put(SESSION_KEY, state);
    }
    
    public WebSessionState getCurrentStateAWT() {
	return (WebSessionState) AppContext.getAppContext().get(SESSION_KEY);
    }

    public synchronized WebSessionState getCurrentState(HttpSession session, int subSessionID) {
	List<WebSessionState> subSessionList = (List<WebSessionState>) session.getAttribute(SESSION_KEY);
	if (subSessionList != null) {
		return subSessionList.get(subSessionID);
	}

	return null;
    }
    
    public synchronized void unregister() {
	// synchronized (stateStore) {
	// WebSessionState state = stateStore.get();
	// stateStore.set(null);
	// }
    }
    
    public void disposeSession(HttpSession session) {
	List<WebSessionState> subSessionList = (List<WebSessionState>) session.getAttribute(SESSION_KEY);
	if(subSessionList != null) {
	    for(WebSessionState state : subSessionList) {
		state.dispose();
	    }
	    subSessionList.clear();
	}
    }
}
