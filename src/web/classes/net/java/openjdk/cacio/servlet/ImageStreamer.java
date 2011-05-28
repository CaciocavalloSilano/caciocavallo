package net.java.openjdk.cacio.servlet;
import java.awt.*;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
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
	// TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
//	try {
//	    Thread.sleep(100);
//	} catch (InterruptedException e) {
//	    e.printStackTrace();
//	}
	
	HttpSession session = request.getSession(false);
	//System.out.println("Image-Stream Session: "+session.getId());
	String subSessionID = request.getParameter("subsessionid");
	
	if(session == null || subSessionID == null) {
	    throw new RuntimeException("Should not reach");
	}

	WebSessionState.register(session, Integer.parseInt(subSessionID));
	try {
	response.setContentType("text/plain");

	ScreenUpdate update = getDirtyRectangle();
	
	if(update != null) {
	    OutputStream str = response.getOutputStream();
	    str.write((update.getX() + ":" + update.getY() + ":").getBytes("UTF-8"));
	    str.write(update.getImageData());
	}
	
	}finally {
	    WebSessionState.unregister();
	}
    }

    int imgCnt = 0;
    protected ScreenUpdate getDirtyRectangle() {

	boolean dataFound = false;
	int cnt = 0;
	while (cnt < 1000) {
	    long start = System.currentTimeMillis();

	    Rectangle unionRect = null;
	    BufferedImage bImg = null;
	    int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
	    WebSessionState state = WebSessionState.getCurrentState();
	    
	    state.lockSession();
	    
	    WebGraphicsConfiguration config = state.getGraphicsConfiguration();
	//    System.out.println("Config: "+config);
	    if(config != null) {
	    WebScreen screen = config.getScreen();
	    WebSurfaceData screenSurface = screen.getSurfaceData();
	    synchronized (screenSurface.dirtyRects) {
		//SunToolkit.awtLock();

		ArrayList<Rectangle> dirtyRectList = screenSurface.dirtyRects;
		// System.out.println("Dirty-Rect-Size: "+dirtyRectList.size());
		
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
		    // BufferedImage bImg = new BufferedImage(unionRect.width,
		    // unionRect.height, BufferedImage.TYPE_INT_RGB);
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

	    
	    state.unlockSession();

	    // SunToolkit.awtUnlock();

	    // g.drawImage(WebSurfaceData.imgBuffer, 0, 0, null);

	    if (bImg != null) {
		byte[] bData = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
		try {
//		    ImageIO.write(bImg, "png", bos);
		    bData = new PngEncoderB(bImg, false, PngEncoder.FILTER_NONE, 2).pngEncode();
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}

//		bData = bos.toByteArray();
		
//		try {
//		    FileOutputStream fos = new FileOutputStream("/home/ce/imgFiles/"+imgCnt+".png");
//		    fos.write(bData);
//		    fos.close();
//		    imgCnt++;
//		}catch(Exception ex) {
//		    ex.printStackTrace();
//		}
		
		byte[] data = Base64Coder.encode(bData);

		long end = System.currentTimeMillis();
//		System.out.println("Dirty-Rect: " + x1 + "/" + x2 + "  -   " + unionRect.width + "/" + unionRect.height + " Data-Size:" + data.length
//			+ " took:" + (end - start));

		return new ScreenUpdate(unionRect.x, unionRect.y, data);
	    }
	    }
	    
	    cnt++;
	    try {
		Thread.sleep(20); //TODO: Lower
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}

	return null;
    }

    // static int cnt = 0;


    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	System.out.println("Got GOST");
	// TODO Auto-generated method stub
    }

}
