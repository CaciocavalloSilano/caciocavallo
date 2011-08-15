/*
 * Copyright (c) 2011, Clemens Eisserer, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package net.java.openjdk.cacio.servlet;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class EventReceiver
 */
public class EventReceiveServlet extends SubSessionServletBase {
   
    public EventReceiveServlet() {
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
