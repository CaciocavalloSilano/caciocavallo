package net.java.openjdk.cacio.servlet;

import java.awt.*;
import java.awt.image.*;
import java.util.List;

public abstract class CmdStreamEncoder {

    protected int uByteToInt(byte signed) {
	int unsigned = signed;
	if (signed < 0) {
	    unsigned = ((int) signed) + (((int) Byte.MAX_VALUE) - ((int) Byte.MIN_VALUE) + 1);
	}

	return unsigned;
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
	
	System.out.println("Packed "+cnt+" areas into image");
    }
    
    public abstract byte[] getEncodedData(List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdData);
}
