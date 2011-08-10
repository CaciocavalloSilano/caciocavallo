package net.java.openjdk.cacio.servlet;

import java.awt.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;

public class AppStarter extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	//TODO: Generalize & re-use
	HttpSession session = request.getSession(false);
	String subSessionID = request.getParameter("subsessionid");

	if (session == null || subSessionID == null) {
	    throw new RuntimeException("Should not reach");
	}
	
	int width = Integer.parseInt(request.getParameter("w"));
	int height = Integer.parseInt(request.getParameter("h"));
	
	WebSessionState state = WebSessionManager.getInstance().getCurrentState(session, Integer.parseInt(subSessionID));
	state.setInitialScreenDimension(new Dimension(width, height));
	
	 new AppContextCreator().startAppInNewAppContext(state);
    }
}
