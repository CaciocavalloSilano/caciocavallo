package net.java.openjdk.cacio.servlet;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.List;

import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;

public abstract class CmdStreamEncoder {
    
    public static final String FORMAT_PNG_BASE64 = "base64";
    public static final String FORMAT_PNG_IMG = "img";
    public static final String FORMAT_PNG_XHR = "png";
    public static final String FORMAT_RLE = "rle";
    
    String contentType;
    
    public CmdStreamEncoder(String contentType) {
	this.contentType = contentType;
    }
    
    protected void copyUpdatesToPackedImage(List<ScreenUpdate> updateList, BufferedImage packedImage, int packedAreaHeight) {
	Graphics g = packedImage.getGraphics();

	int cnt = 0;
	for (ScreenUpdate update : updateList) {
	    if (update instanceof BlitScreenUpdate) {
		BlitScreenUpdate bsUpdate = (BlitScreenUpdate) update;

		int width = bsUpdate.getUpdateArea().getWidth();
		int height = bsUpdate.getUpdateArea().getHeight();

		g.drawImage(bsUpdate.getImage(), bsUpdate.getPackedX(), bsUpdate.getPackedY() + packedAreaHeight, bsUpdate.getPackedX() + width, bsUpdate.getPackedY()+ height + packedAreaHeight, bsUpdate.getSrcX(), bsUpdate.getSrcY(),
			bsUpdate.getSrcX() + width, bsUpdate.getSrcY() + height, null);
		cnt++;
	    }
	}
	
//	System.out.println("Packed "+cnt+" areas into image");
    }
    
    public abstract void writeEncodedData(OutputStream os, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdData) throws IOException;
    
    public abstract void writeEmptyData(OutputStream os) throws IOException;

    public String getContentType() {
        return contentType;
    }
    
    public static CmdStreamEncoder getBackendForName(String backendName) {
	if(backendName.equalsIgnoreCase(FORMAT_RLE)) {
	    return new BinaryRLEStreamEncoder();
	} else if(backendName.equalsIgnoreCase(FORMAT_PNG_XHR)) {
	    return new BinaryPngStreamEncoder();
	} else if(backendName.equalsIgnoreCase(FORMAT_PNG_IMG)) {
	    return new ImageCmdStreamEncoder();
	} else {
	    return new Base64CmdStreamEncoder();
	}
    }
}
