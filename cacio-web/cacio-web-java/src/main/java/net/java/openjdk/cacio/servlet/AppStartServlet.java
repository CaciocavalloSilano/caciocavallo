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

import java.awt.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;
import net.java.openjdk.cacio.servlet.transport.*;

/**
 * Servlet for actually starting an Application, after the environment (e.g.
 * screen-size and supported transport backends) has been set-up accordingly by
 * the SessionInitializeServlet.
 * 
 * Usually called automatically by the client-initialization code, not by the
 * user.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class AppStartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	int width = Integer.parseInt(request.getParameter("w"));
	int height = Integer.parseInt(request.getParameter("h"));
	String transport = request.getParameter("t");

	WebSessionState state = WebSessionManager.getInstance().getSessionState(request);
	if (state != null) {
	    state.setInitialScreenDimension(new Dimension(width, height));
	    state.setBackend(Transport.getTransportForName(transport, state.getCompressLevel()));
	    
	    if(!state.isContextInitialized()) {
		new AppContextCreator().startAppInNewAppContext(state);
	    }
	}
    }
}
