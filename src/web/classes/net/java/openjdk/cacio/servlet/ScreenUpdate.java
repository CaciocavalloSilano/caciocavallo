package net.java.openjdk.cacio.servlet;

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
}
