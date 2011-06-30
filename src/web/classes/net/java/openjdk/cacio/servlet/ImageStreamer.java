package net.java.openjdk.cacio.servlet;

import java.awt.*;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import sun.awt.*;
import net.java.openjdk.awt.peer.web.*;

/**
 * Servlet implementation class ImageStreamer
 */
@WebServlet("/ImageStreamer")
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

	ScreenUpdate update = getDirtyRectangle(session, Integer.parseInt(subSessionID));

	if (update != null) {
	    OutputStream str = response.getOutputStream();
	    response.setContentType("text/plain");
	    str.write((update.getX() + ":" + update.getY() + ":").getBytes("UTF-8"));
	    str.write(update.getImageData());
	}
    }

    int imgCnt = 0;

    protected ScreenUpdate getDirtyRectangle(HttpSession session, int subSessionID) {
	int cnt = 0;
	while (cnt < 1000) {
	    Rectangle unionRect = null;
	    BufferedImage bImg = null;
	    int x1 = 0, y1 = 0, x2 = 0, y2 = 0;

	    WebSessionState state = WebSessionManager.getInstance().getCurrentState(session, subSessionID);
	    WebGraphicsConfiguration config = state.getGraphicsConfiguration();

	    if (config != null) {
		WebScreen screen = config.getScreen();
		WebSurfaceData screenSurface = screen.getSurfaceData();
		synchronized (screenSurface.dirtyRects) {

		    ArrayList<Rectangle> dirtyRectList = screenSurface.dirtyRects;

		    // Zusammenhaengenden bereich finden
		    if (dirtyRectList.size() > 0) {
			unionRect = dirtyRectList.get(0);
			for (Rectangle rect : dirtyRectList) {
			    unionRect = unionRect.union(rect);
			}
			dirtyRectList.clear();
		    }
		}

		if (unionRect != null && unionRect.width > 0 && unionRect.height > 0) {
		    bImg = new BufferedImage(unionRect.width, unionRect.height, BufferedImage.TYPE_INT_RGB);
		    Graphics g = bImg.getGraphics();

		    x1 = unionRect.x;
		    y1 = unionRect.y;
		    x2 = unionRect.x + unionRect.width;
		    y2 = unionRect.y + unionRect.height;

		    /*
		     * dx1 - the x coordinate of the first corner of the
		     * destination rectangle. dy1 - the y coordinate of the
		     * first corner of the destination rectangle. dx2 - the x
		     * coordinate of the second corner of the destination
		     * rectangle. dy2 - the y coordinate of the second corner of
		     * the destination rectangle. sx1 - the x coordinate of the
		     * first corner of the source rectangle. sy1 - the y
		     * coordinate of the first corner of the source rectangle.
		     * sx2 - the x coordinate of the second corner of the source
		     * rectangle. sy2 - the y coordinate of the second corner of
		     * the source rectangle.
		     */

		    g.drawImage(screenSurface.imgBuffer, 0, 0, unionRect.width, unionRect.height, x1, y1, x2, y2, null);
		}

		if (bImg != null) {
		    byte[] bData = null;
		    ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
		    try {
			// ImageIO.write(bImg, "png", bos);
			bData = new PngEncoderB(bImg, false, PngEncoder.FILTER_NONE, 2).pngEncode();

		    } catch (Exception e) {
			e.printStackTrace();
		    }

//
//		     try {
//		     FileOutputStream fos = new
//		     FileOutputStream("/home/ce/imgFiles/" + imgCnt + ".png");
//		     fos.write(bData);
//		     fos.close();
//		     imgCnt++;
//		     } catch (Exception ex) {
//		     ex.printStackTrace();
//		     }

		    byte[] data = Base64Coder.encode(bData);
		    return new ScreenUpdate(unionRect.x, unionRect.y, data);
		}
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
