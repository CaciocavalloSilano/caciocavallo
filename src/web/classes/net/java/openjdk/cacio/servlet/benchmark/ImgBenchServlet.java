package net.java.openjdk.cacio.servlet.benchmark;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class ImgBenchServlet extends HttpServlet {

    byte[] imgData;
    
    public ImgBenchServlet() {
	imgData = loadFileInArray("150.rle");
    }
    
    protected byte[] loadFileInArray(String fileName) {
	try {
	    FileInputStream fin = new FileInputStream(fileName);
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    
	    int read = 0;
	    while((read = fin.read()) != -1) {
		bos.write(read);
	    }
	    
	    fin.close();
	    return bos.toByteArray();
	}catch(IOException ex) {
	    ex.printStackTrace();
	}
	
	return null;
    }
    
    int counter = 0;
    long lastTime = 0;
    @Override
    public synchronized void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	disableCaching(resp);
	resp.setContentType("image/png");
	
	if(counter % 1000 == 0) {
	    if(counter != 0) {
		System.out.println("Time for 1000 images: "+(System.currentTimeMillis() - lastTime));
	    }
	    lastTime = System.currentTimeMillis();
	}
	
//	try {
//	    Thread.sleep(35);
//	} catch (InterruptedException e) {
//	    e.printStackTrace();
//	}
	
	resp.getOutputStream().write(imgData);
	
	counter++;
    }

    protected void disableCaching(HttpServletResponse response) {
	response.setHeader("Expires", "Sat, 1 May 2000 12:00:00 GMT");
	response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
	response.addHeader("Cache-Control", "post-check=0, pre-check=0");
	response.setHeader("Pragma", "no-cache");
    }
}
