package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import net.java.openjdk.awt.peer.web.*;
import net.java.openjdk.cacio.servlet.png.*;

public class ImageCmdStreamEncoder extends CmdStreamEncoder {

    byte[] emptyImgData;
    
    public ImageCmdStreamEncoder() {
	super("image/png");
	BufferedImage emptyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
	emptyImg.setRGB(0, 0, 0);
	emptyImgData = PNGEncoder.getInstance().encode(emptyImg, 2);
    }
    
    protected void encodeImageCmdStream(BufferedImage bImg, List<Integer> cmdList) {
	bImg.setRGB(0, 0, cmdList.size());
	
	for(int i=0; i < cmdList.size(); i++) {
	    int pixelCnt = i+1;
	    int yPos = pixelCnt / bImg.getWidth();
	    int xPos = pixelCnt % bImg.getWidth();
	    
	    int intValue = cmdList.get(i);
	    int r = intValue < 0 ? 1<<16 : 0; //sign
	    int gb = Math.abs(intValue) & 0x0000FFFF;
	    
	    int rgb = r | gb;
	    bImg.setRGB(xPos, yPos, rgb);
	}
    }
    
    public void writeEncodedData(OutputStream os, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList) throws IOException {
	DamageRect packedRegionBox = packer.getBoundingBox();
	int regionWidth = packedRegionBox.getWidth() != 0 ? packedRegionBox.getWidth() : 16;
	int regionHeight = packedRegionBox.getHeight();
	int cmdAreaHeight = (int) Math.ceil(((double) cmdList.size() + 1) / (regionWidth));
	
	BufferedImage packedImage = new BufferedImage(regionWidth, regionHeight + cmdAreaHeight, BufferedImage.TYPE_INT_RGB);
	encodeImageCmdStream(packedImage, cmdList);
	copyUpdatesToPackedImage(pendingUpdateList, packedImage, cmdAreaHeight);
	
	byte[] imgData = PNGEncoder.getInstance().encode(packedImage, 2);
	os.write(imgData);
    }

    @Override
    public void writeEmptyData(OutputStream os) throws IOException {
	os.write(emptyImgData);
    }    
    
    
}
