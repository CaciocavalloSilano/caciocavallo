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
    private static final long serialVersionUID = 1L;

    byte[] emptyImageData;
    
    public ImageStreamer() {
	generateEmptyImageData();
    }
    
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
	
	response.setContentType("image/png");
//	response.setContentType("text/plain");
	disableCaching(response);

	byte[] updateData = getDirtyRectangle(session, Integer.parseInt(subSessionID));
	if (updateData != null) {
	    response.getOutputStream().write(updateData);
	}else {
	    response.getOutputStream().write(emptyImageData);
	}
    }
    
    protected void disableCaching(HttpServletResponse response) {
	response.setHeader("Expires", "Sat, 1 May 2000 12:00:00 GMT");
	response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
	response.addHeader("Cache-Control", "post-check=0, pre-check=0");
	response.setHeader("Pragma", "no-cache");
    }

    int imgCnt = 0;

    protected byte[] getDirtyRectangle(HttpSession session, int subSessionID) {
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

		byte[] updateData = screenSurface.getScreenUpdates();
		if (updateData != null)
		    return updateData;
		// List<ScreenUpdate> updates =
		// screenSurface.getScreenUpdates();
		// if (updates != null)
		// return updates;
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
