package net.java.openjdk.cacio.servlet;

import java.awt.*;
import java.awt.image.*;
import java.util.List;

import com.keypoint.*;

import net.java.openjdk.awt.peer.web.*;

public class ImageCmdStreamEncoder extends CmdStreamEncoder {

    protected void encodeImageCmdStream(BufferedImage bImg, byte[] stream) {
	bImg.setRGB(0, 0, stream.length);
	int i = 0;
	while (i < stream.length) {
	    int pixelCnt = (i / 3) + 1;
	    int yPos = pixelCnt / bImg.getWidth();
	    int xPos = pixelCnt % bImg.getWidth();

	    int r = i < stream.length ? uByteToInt(stream[i++]) << 16 : 0;
	    int g = i < stream.length ? uByteToInt(stream[i++]) << 8 : 0;
	    int b = i < stream.length ? uByteToInt(stream[i++]) : 0;

	    int rgbValue = r | g | b;

	    bImg.setRGB(xPos, yPos, rgbValue);
	}
    }
    
    public byte[] getEncodedData(List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, byte[] cmdData) {
	DamageRect packedRegionBox = packer.getBoundingBox();
	int regionWidth = packedRegionBox.getWidth();
	int regionHeight = packedRegionBox.getHeight();
	int cmdAreaHeight = (int) Math.ceil(((double) cmdData.length + 3) / (regionWidth * 3));
	
	BufferedImage packedImage = new BufferedImage(regionWidth, regionHeight + cmdAreaHeight, BufferedImage.TYPE_INT_RGB);
	encodeImageCmdStream(packedImage, cmdData);
	copyUpdatesToPackedImage(pendingUpdateList, packedImage, cmdAreaHeight);
	
	return new PngEncoderB(packedImage, false, PngEncoder.FILTER_NONE, 2).pngEncode();
    }
    
}
