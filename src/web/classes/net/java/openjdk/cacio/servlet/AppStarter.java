package net.java.openjdk.cacio.servlet;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.swing.text.html.parser.*;

import sun.awt.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class AppStarter
 */
@WebServlet("/AppStarter")
public class AppStarter extends HttpServlet {

    public String startHtml = null;
    private WebSessionState sessionState;

    public AppStarter() {
	loadStartHTML();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String className = request.getParameter("cls");
	HttpSession session = request.getSession();
	System.out.println("Starter-Session: " + session.getId());
	System.out.println("Loading Application Class: " + className);

	WebSessionState state = startAppInNewAppContext(session, className);
	response.setContentType("text/html");
	response.getWriter().write(startHtml.replaceAll("SSID", String.valueOf(state.getSubSessionID())));
    }
    
    protected WebSessionState startAppInNewAppContext(final HttpSession session, final String className) {
	ThreadGroup appGroup = new ThreadGroup(String.valueOf(new Random().nextLong()));

	Thread t = new Thread(appGroup, "AppInitThread") {
	    public void run() {
		AppContext appContext = SunToolkit.createNewAppContext();
		
		WebSessionState state = WebSessionManager.getInstance().register(session);
		sessionState = state;
		try {
		    state.lockSession();

		    ClassLoader loader = getClass().getClassLoader();
		    Class cls = loader.loadClass(className);
		    Method mainMethod = cls.getMethod("main", String[].class);
		    mainMethod.setAccessible(true);
		    // TODO: Handle parameters
		    mainMethod.invoke(cls, (Object) new String[] {});
		} catch (Exception ex) {
		    ex.printStackTrace();
		} finally {
		    state.unlockSession();
		}
	    }
	};
	
	try {
	    t.start();
	    
	    /* Wait for initialization before allowing the servlet to send down its JS
	     *	and trigger polling 
	     */
	    t.join();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	
	return sessionState;
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
