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

import java.util.logging.*;

import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * SubSessionServletBase contains utility functions for dealing with subsession functionality.
 * 
 * @see WebSessionManager
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class SubSessionServletBase extends HttpServlet {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * @param request the request-object of the ongoing HttpRequest
     * @return  WebSessionState assicioated with the current Session/SubSession
     */
    protected WebSessionState getSessionState(HttpServletRequest request) {
	HttpSession session = request.getSession(false);
	String subSessionID = request.getParameter("subsessionid");

	if (session == null || subSessionID == null) {
	    logger.log(Level.WARNING, "No Session registered for the specified session-id/subsession-number. Ignoring request");
	    return null;
	}
	
	return WebSessionManager.getInstance().getCurrentState(session, Integer.parseInt(subSessionID));
    }
}
