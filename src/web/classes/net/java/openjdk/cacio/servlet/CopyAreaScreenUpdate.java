package net.java.openjdk.cacio.servlet;

import java.io.*;

public class CopyAreaScreenUpdate extends ScreenUpdate {

    int w, h, dx, dy;
    
    public CopyAreaScreenUpdate(int x, int y, int w, int h, int dx, int dy) {
	super(x, y, null);
	
	this.w = w;
	this.h = h;
	this.dx = dx;
	this.dy = dy;
    }

    public void writeToStream(OutputStream str) throws IOException {
	str.write(("c:" + x + ":" + y + ":" + w + ":" + h + ":" + dx +":" + dy + ":").getBytes("UTF-8"));
    }
}
