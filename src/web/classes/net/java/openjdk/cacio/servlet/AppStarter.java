package net.java.openjdk.cacio.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class AppStarter
 */
public class AppStarter extends HttpServlet {

    String startHtml = null;

    public AppStarter() throws Exception {
	startHtml = loadStartHTML();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String className = request.getParameter("cls");
	String[] params = generateParameterArray(request);
	HttpSession session = request.getSession();
	System.out.println("Starter-Session: " + session.getId());
	System.out.println("Loading Application Class: " + className);

	WebSessionState state = new AppContextCreator().startAppInNewAppContext(session, className, params);
	response.setContentType("text/html");
	response.getWriter().write(startHtml.replaceAll("SSID", String.valueOf(state.getSubSessionID())));
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
