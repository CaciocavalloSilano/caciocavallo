package net.java.openjdk.cacio.servlet;

import java.util.*;
import java.util.logging.*;

import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;

public class TestSessionManager extends WebSessionManager {
    
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    HashMap<Integer, WebSessionState> idSessionMap = new HashMap<Integer, WebSessionState>();

    public synchronized WebSessionState register(HttpSession session) {
	int subSessionID = idSessionMap.size();
	WebSessionState sessionState = new WebSessionState(subSessionID, idSessionMap);
	idSessionMap.put(subSessionID, sessionState);

	return sessionState;
    }
    
    public synchronized WebSessionState getCurrentState(HttpSession session, int subSessionID) {
	    WebSessionState state = idSessionMap.get(subSessionID);
	    registerSessionAtCurrentThread(state);
	    
	    return state;
    }
    
    public synchronized void disposeSession(HttpSession session) {
    }
    
    public synchronized void disposeSession(int subSessionID) {
	WebSessionState state = idSessionMap.remove(subSessionID);
	state.dispose();
    }
    
    public WebSessionState getSessionState(HttpServletRequest request) {
	String subSessionID = request.getParameter("subsessionid");
	return getCurrentState(null, Integer.parseInt(subSessionID));
    }
}
