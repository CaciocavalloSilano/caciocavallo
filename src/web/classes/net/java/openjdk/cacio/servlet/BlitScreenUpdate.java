package net.java.openjdk.cacio.servlet;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import net.java.openjdk.awt.peer.web.*;

public class BlitScreenUpdate extends ScreenUpdate {

    int srcX, srcY;
    
    BufferedImage image;
    
    public BlitScreenUpdate(int dstX, int dstY, int srcX, int srcY, int w, int h, BufferedImage src) {
	super(new DamageRect(dstX, dstY, dstX + w, dstY + h));
	
	this.srcX = srcX;
	this.srcY = srcY;
	
	image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	Graphics g = image.getGraphics();
	g.drawImage(src, 0, 0, w, h, srcX, srcY, srcX + w, srcY + h, null);
    }
    
    public void writeCmdStream(DataOutputStream dos) {
	try {
	    dos.writeShort(0);
	    dos.writeShort((short) updateArea.getX1());
	    dos.writeShort((short) updateArea.getY1());
	    dos.writeShort((short) updateArea.getX2());
	    dos.writeShort((short) updateArea.getY2());
	    dos.writeShort((short) srcX);
	    dos.writeShort((short) srcY);
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
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

    public BufferedImage getImage() {
        return image;
    }
}
