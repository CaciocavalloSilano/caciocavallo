package net.java.openjdk.cacio.servlet;

import java.io.*;

public class ScreenUpdate {
    int x, y;
    byte[] imageData;
 

    public ScreenUpdate(int x, int y, byte[] imageData) {
	this.x = x;
	this.y = y;
	this.imageData = imageData;
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public byte[] getImageData() {
	return imageData;
    }

    public void writeToStream(OutputStream str) throws IOException {
	str.write(("i:" + x + ":" + y + ":").getBytes("UTF-8"));
	str.write(getImageData());
	str.write(':');
    }
}
