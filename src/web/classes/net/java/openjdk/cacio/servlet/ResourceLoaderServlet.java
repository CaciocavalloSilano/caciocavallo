package net.java.openjdk.cacio.servlet;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/ResourceLoader")
public class ResourceLoaderServlet extends HttpServlet {

    String startHtml = null;

    public ResourceLoaderServlet() throws Exception {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String resourceName = request.getParameter("res");
	resourceName = '/' + resourceName.replace('_', '/');
	
	OutputStream os = response.getOutputStream();
	InputStream is = new BufferedInputStream(getClass().getResourceAsStream(resourceName));
	int read;
	while((read = is.read()) != -1) {
	    os.write(read);
	}
    }
}

