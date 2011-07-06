package net.java.openjdk.awt.peer.web;

public class DamageRect {
    int x1, y1, x2, y2;

    public DamageRect() {

    }
    
    public DamageRect(DamageRect rect) {
	this(rect.x1, rect.y1, rect.x2, rect.y2);
    }
    
    public DamageRect(int x1, int y1, int x2, int y2) {
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = x2;
	this.y2 = y2;
    }

    public int getX1() {
	return x1;
    }

    public int getY1() {
	return y1;
    }

    public int getX2() {
	return x2;
    }

    public int getY2() {
	return y2;
    }

    public void setX1(int x1) {
	this.x1 = x1;
    }

    public void setY1(int y1) {
	this.y1 = y1;
    }

    public void setX2(int x2) {
	this.x2 = x2;
    }

    public void setY2(int y2) {
	this.y2 = y2;
    }

    public void union(DamageRect rect2) {
	x1 = Math.min(x1, rect2.x1);
	y1 = Math.min(y1, rect2.y1);
	x2 = Math.max(x2, rect2.x2);
	y2 = Math.max(y2, rect2.y2);
    }
    
    public void restrictToArea(int rx1, int ry1, int rx2, int ry2) {
	x1 = Math.max(x1, rx1);
	y1 = Math.max(y1, ry1);
	x2 = Math.min(x2, rx2);
	y2 = Math.min(y2, ry2);
    }
    
    
    public int getWidth() {
	return x2 - x1;
    }
    
    public int getHeight() {
	return y2 - y1;
    }
    
    public String toString() {
	return "x1:"+x1+" y1:"+y1+" x2:"+x2+" y2:"+y2+"   w:"+getWidth()+" h:"+getHeight();
    }
}
