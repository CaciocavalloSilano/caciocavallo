package net.java.openjdk.cacio.server;

import net.java.openjdk.cacio.servlet.*;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.*;

public class CacioServer {

    public CacioServer() throws Exception {
        Server server = new Server(8080);
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("bin/");
        
        ResourceHandler handler = new ResourceHandler();
        handler.setResourceBase("bin");
        handler.setServer(server);
 
        context.addServlet(new ServletHolder(new AppStarter()),"/AppStarter");
        context.addServlet(new ServletHolder(new ImageStreamer()),"/ImageStreamer");
        context.addServlet(new ServletHolder(new EventReceiver()),"/EventReceiver");
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { handler, context });
        server.setHandler(handlers);
        
        server.start();
        server.join();
    }
    
    public static void main(String[] args) throws Exception {
	new CacioServer();
    }

}


