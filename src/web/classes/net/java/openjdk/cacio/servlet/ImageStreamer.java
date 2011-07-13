package net.java.openjdk.cacio.servlet;


import java.io.*;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class ImageStreamer
 */
public class ImageStreamer extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageStreamer() {
	super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	HttpSession session = request.getSession(false);
	String subSessionID = request.getParameter("subsessionid");

	if (session == null || subSessionID == null) {
	    throw new RuntimeException("Should not reach");
	}

	List<ScreenUpdate> updateList = getDirtyRectangle(session, Integer.parseInt(subSessionID));

	if (updateList != null) {
	    OutputStream str = response.getOutputStream();
	    response.setContentType("text/plain");
	    for (ScreenUpdate update : updateList) {
		update.writeToStream(str);
	    }
	    // System.out.println();
	}
    }

    int imgCnt = 0;

    protected List<ScreenUpdate> getDirtyRectangle(HttpSession session, int subSessionID) {
	int cnt = 0;

	try {
	    Thread.sleep(50);
	} catch (InterruptedException e1) {
	    e1.printStackTrace();
	}

	while (cnt < 1000) {
	    WebSessionState state = WebSessionManager.getInstance().getCurrentState(session, subSessionID);
	    WebGraphicsConfiguration config = state.getGraphicsConfiguration();

	    if (config != null) {
		WebScreen screen = config.getScreen();
		WebSurfaceData screenSurface = screen.getSurfaceData();

		List<ScreenUpdate> updates = screenSurface.getScreenUpdates();
		if (updates != null)
		    return updates;
	    }

	    cnt++;
	    try {
		Thread.sleep(20);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}

	return null;
    }
}
