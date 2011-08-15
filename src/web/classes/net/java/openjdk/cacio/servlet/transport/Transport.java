package net.java.openjdk.cacio.servlet.transport;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.List;

import net.java.openjdk.awt.peer.web.*;

public abstract class Transport {
    
    public static final String FORMAT_PNG_BASE64 = "base64";
    public static final String FORMAT_PNG_IMG = "img";
    public static final String FORMAT_PNG_XHR = "png";
    public static final String FORMAT_RLE = "rle";
    
    String contentType;
    
    public Transport(String contentType) {
	this.contentType = contentType;
    }
    
    protected void copyUpdatesToPackedImage(List<ScreenUpdate> updateList, BufferedImage packedImage, int packedAreaHeight) {
	Graphics g = packedImage.getGraphics();

	for (ScreenUpdate update : updateList) {
	    if (update instanceof BlitScreenUpdate) {
		BlitScreenUpdate bsUpdate = (BlitScreenUpdate) update;

		int width = bsUpdate.getUpdateArea().getWidth();
		int height = bsUpdate.getUpdateArea().getHeight();

		g.drawImage(bsUpdate.getImage(), bsUpdate.getPackedX(), bsUpdate.getPackedY() + packedAreaHeight, bsUpdate.getPackedX() + width, bsUpdate.getPackedY()+ height + packedAreaHeight, bsUpdate.getSrcX(), bsUpdate.getSrcY(),
			bsUpdate.getSrcX() + width, bsUpdate.getSrcY() + height, null);
	    }
	}
    }
    
    public abstract void writeEncodedData(OutputStream os, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdData) throws IOException;
    
    public abstract void writeEmptyData(OutputStream os) throws IOException;

    public String getContentType() {
        return contentType;
    }
    
    public static Transport getBackendForName(String backendName) {
	if(backendName.equalsIgnoreCase(FORMAT_RLE)) {
	    return new BinaryRLETransport();
	} else if(backendName.equalsIgnoreCase(FORMAT_PNG_XHR)) {
	    return new BinaryPngTransport();
	} else if(backendName.equalsIgnoreCase(FORMAT_PNG_IMG)) {
	    return new ImageTransport();
	} else {
	    return new Base64PngTransport();
	}
    }
}
