package net.java.openjdk.awt.peer.web;

import java.awt.*;
import java.awt.image.*;
import java.util.List;

public class BlitScreenUpdate extends ScreenUpdate {

    int packedX, packedY;
    int srcX, srcY;

    BufferedImage image;
    boolean isEvacuated;

    public BlitScreenUpdate(int dstX, int dstY, int srcX, int srcY, int w, int h, BufferedImage src) {
	super(new WebRect(dstX, dstY, dstX + w, dstY + h));

	this.srcX = srcX;
	this.srcY = srcY;
	this.isEvacuated = false;

	this.image = src;
    }

    public void evacuate() {
	if (!isEvacuated) {
	    BufferedImage src = image;

	    int w = getUpdateArea().getWidth();
	    int h = getUpdateArea().getHeight();

	    image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    Graphics g = image.getGraphics();
	    g.drawImage(src, 0, 0, w, h, srcX, srcY, srcX + w, srcY + h, null);
	    setSrcX(0);
	    setSrcY(0);
	    
	    isEvacuated = true;
	}
    }

    public void writeCmdStream(List<Integer> cmdList) {
	cmdList.add(0);
	cmdList.add(updateArea.getX1());
	cmdList.add(updateArea.getY1());
	cmdList.add(updateArea.getX2());
	cmdList.add(updateArea.getY2());
	cmdList.add(packedX);
	cmdList.add(packedY);
    }

    public BufferedImage getImage() {
	return image;
    }

    public int getPackedX() {
	return packedX;
    }

    public int getPackedY() {
	return packedY;
    }

    public void setPackedX(int packedX) {
	this.packedX = packedX;
    }

    public void setPackedY(int packedY) {
	this.packedY = packedY;
    }

    public int getSrcX() {
	return srcX;
    }

    public int getSrcY() {
	return srcY;
    }

    public void setSrcX(int srcX) {
	this.srcX = srcX;
    }

    public void setSrcY(int srcY) {
	this.srcY = srcY;
    }
}
