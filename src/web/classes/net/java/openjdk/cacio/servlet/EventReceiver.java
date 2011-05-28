package net.java.openjdk.cacio.servlet;


import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class EventReceiver
 */
@WebServlet("/EventReceiver")
public class EventReceiver extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EventReceiver() {
	super();
	// TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	// TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected  void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
//	try {
//	    Thread.sleep(100);
//	} catch (InterruptedException e) {
//	    e.printStackTrace();
//	}
//	
	String parameters = request.getParameter("events");
	HttpSession session = request.getSession(false);
	//System.out.println("Image-Stream Session: " + session.getId());
	String subSessionID = request.getParameter("subsessionid");

	if (session == null || subSessionID == null) {
	    throw new RuntimeException("Should not reach");
	}
	WebSessionState.register(session, Integer.parseInt(subSessionID));

	WebSessionState currentState = WebSessionState.getCurrentState();
	currentState.lockSession();

	WebScreen screen = currentState.getGraphicsConfiguration().getScreen();

	parseEventData(parameters, screen);

	currentState.unlockSession();
	WebSessionState.unregister();
    }

    protected void parseEventData(String paramStr, WebScreen screen) {
	String[] params = paramStr.split("_");

//	System.out.println("Event-Anfrage: " + paramStr);

	LinkedList<String> eventDataList = new LinkedList<String>();
	eventDataList.addAll(Arrays.asList(params));

	while (eventDataList.size() > 0) {
	    String command = eventDataList.removeFirst();
	    if (command.length() > 0) {

		if (command.equals("M")) {
		    processMouseEvent(eventDataList);
		} else if (command.equals("MM")) {
		    processMouseMotionEvent(eventDataList);
		} else 
		if(command.equals("K")) {
		    processKeyEvent(eventDataList);
		}
	    }
	}
    }

    protected void processKeyEvent(LinkedList<String> params) {
	boolean down = Integer.parseInt(params.removeFirst()) > 0;
	int keySym = Integer.parseInt(params.removeFirst());
//	char charVal = (char) Integer.parseInt(params.removeFirst());
	char charVal = 0;
	boolean ctrl = Boolean.parseBoolean(params.removeFirst());
	boolean shift = Boolean.parseBoolean(params.removeFirst());
	boolean alt = Boolean.parseBoolean(params.removeFirst());
	WebSessionState.getCurrentState().getKeyboardTracker().trackKeyEvent(down, keySym, charVal, ctrl, shift, alt);
    }

    protected void processMouseMotionEvent(LinkedList<String> params) {
	int x = Integer.parseInt(params.removeFirst());
	int y = Integer.parseInt(params.removeFirst());

	WebSessionState.getCurrentState().getMouseTracker().trackMouseMotionEvent(x, y);
    }

    protected void processMouseEvent(LinkedList<String> params) {
	int x = Integer.parseInt(params.removeFirst());
	int y = Integer.parseInt(params.removeFirst());
	boolean down = Integer.parseInt(params.removeFirst()) > 0;
	int buttonMask = Integer.parseInt(params.removeFirst());
	
	WebSessionState.getCurrentState().getMouseTracker().trackMouseEvent(down, buttonMask, x, y);
    }

}
