package net.java.openjdk.cacio.server;

import java.util.*;

import javax.servlet.http.*;

import net.java.openjdk.cacio.servlet.*;
import net.java.openjdk.cacio.servlet.benchmark.*;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.component.*;

public class CacioServer {

    public CacioServer() throws Exception {
	applySystemProperties();
	
        Server server = new Server(8080);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("bin/");
        context.getSessionHandler().getSessionManager().setMaxInactiveInterval(60);
        context.getSessionHandler().addEventListener(new CacioSessionListener());
        
        ResourceHandler handler = new ResourceHandler();
        handler.setResourceBase("bin");
        handler.setServer(server);
 
//        context.addServlet(new ServletHolder(new ImgBenchServlet()), "/ImageStreamer");
        
        context.addServlet(new ServletHolder(new AppStarter()),"/AppStarter");
        context.addServlet(new ServletHolder(new ImageStreamer()),"/ImageStreamer");
        context.addServlet(new ServletHolder(new EventReceiver()),"/EventReceiver");
        context.addServlet(new ServletHolder(new ResourceLoaderServlet()),"/ResourceLoader");
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { handler, context });
        server.setHandler(handlers);
        
        
        server.start();
        server.join();
    }
    
    protected void applySystemProperties() {
	System.setProperty("awt.useSystemAAFontSettings", "on");
	System.setProperty("awt.toolkit", "net.java.openjdk.awt.peer.web.WebToolkit");
	System.setProperty("java.awt.graphicsenv", "net.java.openjdk.awt.peer.web.WebGraphicsEnvironment");
	System.setProperty("sun.font.fontmanager", "net.java.openjdk.awt.peer.web.WebFontManager");
    }
    
    public static void main(String[] args) throws Exception {
	new CacioServer();
    }

}


