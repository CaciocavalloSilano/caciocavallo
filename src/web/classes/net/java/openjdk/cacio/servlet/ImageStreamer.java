package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.keypoint.*;

import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class ImageStreamer
 */
public class ImageStreamer extends HttpServlet {

    byte[] emptyImageData;

    protected void generateEmptyImageData() {
	BufferedImage emptyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	emptyImg.setRGB(0, 0, 0);
	emptyImageData = new PngEncoderB(emptyImg, false, PngEncoder.FILTER_NONE, 2).pngEncode();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	HttpSession session = request.getSession(false);
	String subSessionID = request.getParameter("subsessionid");

	if (session == null || subSessionID == null) {
	    throw new RuntimeException("Should not reach");
	}

	disableCaching(response);

	WebSessionState state = WebSessionManager.getInstance().getCurrentState(session, Integer.parseInt(subSessionID));
	WebGraphicsConfiguration config = state.getGraphicsConfiguration();

	if (config != null) {
	    WebScreen screen = config.getScreen();
	    WebSurfaceData screenSurface = screen.getSurfaceData();

	    screenSurface.pollForScreenUpdates(response, 15000, 20);
	}
    }

    protected void disableCaching(HttpServletResponse response) {
	response.setHeader("Expires", "Sat, 1 May 2000 12:00:00 GMT");
	response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
	response.addHeader("Cache-Control", "post-check=0, pre-check=0");
	response.setHeader("Pragma", "no-cache");
    }
}
