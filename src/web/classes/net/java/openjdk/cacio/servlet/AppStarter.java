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

    public AppStarter() {
	loadStartHTML();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String className = request.getParameter("cls");
	HttpSession session = request.getSession();
	System.out.println("Starter-Session: " + session.getId());
	System.out.println("Loading Application Class: " + className);

	WebSessionState state = new AppContextCreator().startAppInNewAppContext(session, className);
	response.setContentType("text/html");
	response.getWriter().write(startHtml.replaceAll("SSID", String.valueOf(state.getSubSessionID())));
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
