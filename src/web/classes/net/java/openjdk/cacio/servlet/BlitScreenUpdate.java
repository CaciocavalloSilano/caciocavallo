package net.java.openjdk.cacio.servlet;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import net.java.openjdk.awt.peer.web.*;

public class BlitScreenUpdate extends ScreenUpdate {

    int packedX, packedY;
    int srcX, srcY;

    BufferedImage image;
    boolean isEvacuated;

    public BlitScreenUpdate(int dstX, int dstY, int srcX, int srcY, int w, int h, BufferedImage src) {
	super(new DamageRect(dstX, dstY, dstX + w, dstY + h));

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

    public void writeCmdStream(DataOutputStream dos) {
	try {
	    dos.writeShort(0);
	    dos.writeShort((short) updateArea.getX1());
	    dos.writeShort((short) updateArea.getY1());
	    dos.writeShort((short) updateArea.getX2());
	    dos.writeShort((short) updateArea.getY2());
	    dos.writeShort((short) packedX);
	    dos.writeShort((short) packedY);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
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
