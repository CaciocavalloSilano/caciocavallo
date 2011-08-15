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

package net.java.openjdk.cacio.server;

import net.java.openjdk.cacio.servlet.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.*;

/**
 * Server-Startup class for Caciocavallo-Web. Initializes a builtin Jetty
 * webserver, and sets the appropriate system properties.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class CacioServer {

    public CacioServer() throws Exception {
	this(8080);
    }

    /**
     * Initializes buitin http-server on the specified port, and sets system
     * properties.
     * 
     * @param port
     * @throws Exception
     */
    public CacioServer(int port) throws Exception {
	applySystemProperties();

	Server server = new Server(port);

	ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
	context.setContextPath("/");
	context.setResourceBase("bin/");
	context.getSessionHandler().getSessionManager().setMaxInactiveInterval(30);
	context.getSessionHandler().addEventListener(new CacioSessionListener());

	ResourceHandler handler = new ResourceHandler();
	handler.setResourceBase("bin");
	handler.setServer(server);

	// context.addServlet(new ServletHolder(new ImgBenchServlet()),
	// "/ImageStreamer");

	context.addServlet(new ServletHolder(new SessionInitializeServlet()), "/SessionInitializer");
	context.addServlet(new ServletHolder(new AppStartServlet()), "/AppStarter");
	context.addServlet(new ServletHolder(new ImageStreamer()), "/ImageStreamer");
	context.addServlet(new ServletHolder(new EventReceiveServlet()), "/EventReceiver");
	context.addServlet(new ServletHolder(new ResourceLoaderServlet()), "/ResourceLoader");

	HandlerList handlers = new HandlerList();
	handlers.setHandlers(new Handler[] { handler, context });
	server.setHandler(handlers);

	server.start();
	server.join();
    }

    /**
     * Set the appropriate system properties, so the user doesn't have to
     * specify all the caciocavallo-web classnames by hand.
     */
    protected void applySystemProperties() {
	System.setProperty("awt.useSystemAAFontSettings", "on");
	System.setProperty("awt.toolkit", "net.java.openjdk.awt.peer.web.WebToolkit");
	System.setProperty("java.awt.graphicsenv", "net.java.openjdk.awt.peer.web.WebGraphicsEnvironment");
	System.setProperty("sun.font.fontmanager", "net.java.openjdk.awt.peer.web.WebFontManager");
    }

    public static void main(String[] args) throws Exception {
	int port = Integer.getInteger("cacio.web.port", 8080);
	new CacioServer(port);
    }

}
