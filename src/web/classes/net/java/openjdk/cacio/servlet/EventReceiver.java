package net.java.openjdk.cacio.servlet;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class EventReceiver
 */
public class EventReceiver extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EventReceiver() {
	super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	String parameters = request.getParameter("events");
	HttpSession session = request.getSession(false);
	String subSessionIDString = request.getParameter("subsessionid");

	if (session == null || subSessionIDString == null) {
	    throw new RuntimeException("Should not reach");
	}

	int subSessionID = Integer.parseInt(subSessionIDString);
	WebSessionState currentState = WebSessionManager.getInstance().getCurrentState(session, subSessionID);
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
		} else if (command.equals("K")) {
		    processKeyEvent(state, eventDataList);
		}
	    }
	}
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

    protected void processMouseEvent(WebSessionState state, LinkedList<String> params) {
	int x = Integer.parseInt(params.removeFirst());
	int y = Integer.parseInt(params.removeFirst());
	boolean down = Integer.parseInt(params.removeFirst()) > 0;
	int buttonMask = Integer.parseInt(params.removeFirst());

	state.getMouseTracker().trackMouseEvent(down, buttonMask, x, y);
    }

}
