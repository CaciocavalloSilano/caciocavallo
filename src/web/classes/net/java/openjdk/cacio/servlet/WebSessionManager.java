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

import java.util.*;

import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;
import sun.awt.*;

/**
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
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
    
    public synchronized void disposeSession(HttpSession session) {
	List<WebSessionState> subSessionList = (List<WebSessionState>) session.getAttribute(SESSION_KEY);
	if(subSessionList != null) {
	    for(WebSessionState state : subSessionList) {
		state.dispose();
	    }
	    subSessionList.clear();
	}
    }
}
