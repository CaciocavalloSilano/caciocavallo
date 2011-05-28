package net.java.openjdk.cacio.servlet;


import java.io.*;
import java.lang.reflect.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class AppStarter
 */
@WebServlet("/AppStarter")
public class AppStarter extends HttpServlet {

    public String startHtml = null;
    
    public AppStarter() {
	loadStartHTML();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String className = request.getParameter("cls");
	HttpSession session = request.getSession();
	System.out.println("Starter-Session: "+session.getId());
	
	Integer subsessionID = WebSessionState.register(session, null);
	WebSessionState state = WebSessionState.getCurrentState();
	//TODO: Adapt URLs
	
	try {
	    state.lockSession();
	    Class cls = getClass().forName(className);
	//    synchronized(SDLSurfaceData.dirtyRects) {
		Method mainMethod = cls.getMethod("main", String[].class);
		mainMethod.invoke(cls, (Object) new String[]{"-runs=5",  "-delay=10",  "-screen=5"});
	   // }
	} catch (Exception ex) {
	    ex.printStackTrace();
	} finally {
	    state.unlockSession();
	    WebSessionState.unregister();
	}
	
	response.setContentType("text/html");
	response.getWriter().write(startHtml.replaceAll("SSID", String.valueOf(subsessionID)));
    }
    
    protected void loadStartHTML() {
	try {
	    Reader r = new InputStreamReader(getClass().getResourceAsStream("/StreamBase.html"), "UTF-8");
	    char[] data = new char[32768];
	    int read = r.read(data, 0, data.length);
	    startHtml = new String(data, 0, read);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
