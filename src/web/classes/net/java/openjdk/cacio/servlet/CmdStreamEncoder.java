package net.java.openjdk.cacio.servlet;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.List;

import javax.servlet.http.*;

public abstract class CmdStreamEncoder {
    
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
    
    public abstract void writeEnocdedData(HttpServletResponse response, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdData) throws IOException;
    
    public abstract void writeEmptyData(HttpServletResponse response) throws IOException;
}
