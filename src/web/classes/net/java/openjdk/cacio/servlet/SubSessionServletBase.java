package net.java.openjdk.cacio.servlet;


import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

public class SubSessionServletBase extends HttpServlet {

    protected WebSessionState getSessionState(HttpServletRequest request) {
	HttpSession session = request.getSession(false);
	String subSessionID = request.getParameter("subsessionid");

	if (session == null || subSessionID == null) {
	    throw new RuntimeException("Should not reach");
	}
	
	return WebSessionManager.getInstance().getCurrentState(session, Integer.parseInt(subSessionID));
    }
}
