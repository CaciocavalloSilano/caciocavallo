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

import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class AppStarter
 */
public class SessionInitializeServlet extends SubSessionServletBase {

    String startHtml = null;

    public SessionInitializeServlet() throws Exception {
	startHtml = loadStartHTML();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String className = request.getParameter("cls");
	String format = request.getParameter("format");

	format = (format != null && format.trim().length() > 0) ? format.toLowerCase() : "rle";

	HttpSession session = request.getSession();
	System.out.println("Starter-Session: " + session.getId());
	System.out.println("Loading Application Class: " + className);

	WebSessionState state = WebSessionManager.getInstance().register(session);
	state.setCmdLineParams(generateParameterArray(request));
	state.setMainClsName(className);
	state.setCompressLevel(getCompressionLevel(request));

	response.setContentType("text/html");
	String ssidStartHtml = startHtml.replaceAll("SSID", String.valueOf(state.getSubSessionID()));
	ssidStartHtml = ssidStartHtml.replaceAll("IMGFORMAT", "\"" + format + "\"");
	response.getWriter().write(ssidStartHtml);
    }

    protected int getCompressionLevel(HttpServletRequest request) {
	String cLevelStr = request.getParameter("clevel");
	int cLevel = 2;

	if (cLevelStr != null) {
	    try {
		cLevel = Integer.parseInt(cLevelStr);
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	return cLevel;
    }

    protected String[] generateParameterArray(HttpServletRequest request) {
	ArrayList<String> paramList = new ArrayList<String>();

	String paramValue = null;
	while ((paramValue = request.getParameter("param" + paramList.size())) != null) {
	    paramList.add(paramValue);
	}

	return paramList.toArray(new String[paramList.size()]);
    }

    protected String loadStartHTML() throws Exception {
	StringBuilder htmlBuilder = new StringBuilder(8192);
	Reader r = new InputStreamReader(getClass().getResourceAsStream("/StreamBase.html"), "UTF-8");
	int read;
	while ((read = r.read()) != -1) {
	    htmlBuilder.append((char) read);
	}
	return htmlBuilder.toString();
    }
}
