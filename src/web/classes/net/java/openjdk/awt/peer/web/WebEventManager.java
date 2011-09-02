package net.java.openjdk.awt.peer.web;

import java.util.*;

public class WebEventManager {
    
   WebMouseStateTracker mouseTracker;
   WebKeyboardStateTracker keyboardTracker;
    
   public WebEventManager(WebScreen	 screen) {
	mouseTracker = new WebMouseStateTracker(screen);
	keyboardTracker = new WebKeyboardStateTracker(screen);
   }
   
   public void parseEventData(String paramStr, WebSessionState state) {
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
		} else if (command.equals("MW")) {
		    processMouseWheelEvent(state, eventDataList);
		} else if (command.equals("K")) {
		    processKeyEvent(state, eventDataList);
		} else if (command.equals("S")) {
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
	keyboardTracker.trackKeyEvent(down, keySym, charVal, ctrl, shift, alt);
   }

   protected void processMouseMotionEvent(WebSessionState state, LinkedList<String> params) {
	int x = Integer.parseInt(params.removeFirst());
	int y = Integer.parseInt(params.removeFirst());

	mouseTracker.trackMouseMotionEvent(x, y);
   }

   protected void processMouseWheelEvent(WebSessionState state, LinkedList<String> params) {
	boolean up = Integer.parseInt(params.removeFirst()) > 0;
	int x = Integer.parseInt(params.removeFirst());
	int y = Integer.parseInt(params.removeFirst());

	mouseTracker.trackMouseWheelEvent(up, x, y);
   }

   protected void processMouseEvent(WebSessionState state, LinkedList<String> params) {
	int x = Integer.parseInt(params.removeFirst());
	int y = Integer.parseInt(params.removeFirst());
	boolean down = Integer.parseInt(params.removeFirst()) > 0;
	int buttonMask = Integer.parseInt(params.removeFirst());

	mouseTracker.trackMouseEvent(down, buttonMask, x, y);
   }
}
