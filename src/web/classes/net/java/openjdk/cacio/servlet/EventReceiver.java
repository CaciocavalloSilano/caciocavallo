package net.java.openjdk.cacio.servlet;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class EventReceiver
 */
public class EventReceiver extends SubSessionServletBase {
   
    public EventReceiver() {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String parameters = request.getParameter("events");
	WebSessionState currentState = getSessionState(request);
	try {
	    currentState.lockSession();

	    WebScreen screen = currentState.getGraphicsConfiguration().getScreen();
	    parseEventData(parameters, currentState, screen);
	} finally {
	    currentState.unlockSession();
	}
    }

    protected void parseEventData(String paramStr, WebSessionState state, WebScreen screen) {
	String[] params = paramStr.split("_");

	LinkedList<String> eventDataList = new LinkedList<String>();
	eventDataList.addAll(Arrays.asList(params));

	while (eventDataList.size() > 0) {
	    String command = eventDataList.removeFirst();
	    if (command.length() > 0) {

		if (command.equals("M")) {
		    processMouseEvent(state, eventDataList);
		} else if (command.equals("MM")) {
		    processMouseMotionEvent(state, eventDataList);
		} else if(command.equals("MW")) {
		    processMouseWheelEvent(state, eventDataList);
		} else if (command.equals("K")) {
		    processKeyEvent(state, eventDataList);
		} else if(command.equals("S")) {
		    processResizeEvent(state, eventDataList);
		}
	    }
	}
    }
    
    protected void processResizeEvent(WebSessionState state, LinkedList<String> params) {
	int w = Integer.parseInt(params.removeFirst());
	int h = Integer.parseInt(params.removeFirst());
	
	state.getGraphicsConfiguration().getScreen().resizeScreen(w, h);
    }

    protected void processKeyEvent(WebSessionState state, LinkedList<String> params) {
	boolean down = Integer.parseInt(params.removeFirst()) > 0;
	int keySym = Integer.parseInt(params.removeFirst());
	char charVal = 0;
	boolean ctrl = Boolean.parseBoolean(params.removeFirst());
	boolean shift = Boolean.parseBoolean(params.removeFirst());
	boolean alt = Boolean.parseBoolean(params.removeFirst());
	state.getKeyboardTracker().trackKeyEvent(down, keySym, charVal, ctrl, shift, alt);
    }

    protected void processMouseMotionEvent(WebSessionState state, LinkedList<String> params) {
	int x = Integer.parseInt(params.removeFirst());
	int y = Integer.parseInt(params.removeFirst());

	state.getMouseTracker().trackMouseMotionEvent(x, y);
    }

    protected void processMouseWheelEvent(WebSessionState state, LinkedList<String> params) {
	boolean up = Integer.parseInt(params.removeFirst()) > 0;
	int x = Integer.parseInt(params.removeFirst());
	int y = Integer.parseInt(params.removeFirst());

	state.getMouseTracker().trackMouseWheelEvent(up, x, y);
    }
    
    protected void processMouseEvent(WebSessionState state, LinkedList<String> params) {
	int x = Integer.parseInt(params.removeFirst());
	int y = Integer.parseInt(params.removeFirst());
	boolean down = Integer.parseInt(params.removeFirst()) > 0;
	int buttonMask = Integer.parseInt(params.removeFirst());

	state.getMouseTracker().trackMouseEvent(down, buttonMask, x, y);
    }

}
