package net.java.openjdk.cacio.servlet;

import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class AppStarter
 */
public class SessionInitializer extends HttpServlet {

    String startHtml = null;

    public SessionInitializer() throws Exception {
	startHtml = loadStartHTML();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String className = request.getParameter("cls");

	HttpSession session = request.getSession();
	System.out.println("Starter-Session: " + session.getId());
	System.out.println("Loading Application Class: " + className);

	WebSessionState state = WebSessionManager.getInstance().register(session);
	state.setCmdLineParams(generateParameterArray(request));
	state.setMainClsName(className);
	
	response.setContentType("text/html");
	String ssidStartHtml = startHtml.replaceAll("SSID", String.valueOf(state.getSubSessionID()));
	response.getWriter().write(ssidStartHtml);
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
