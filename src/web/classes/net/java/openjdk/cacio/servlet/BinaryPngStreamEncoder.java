package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;

import com.keypoint.*;

public class BinaryPngStreamEncoder extends BinaryCmdStreamEncoder {
    
    @Override
    public void writeEnocdedData(HttpServletResponse response, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList) throws IOException {
	DamageRect packedRegionBox = packer.getBoundingBox();
	BufferedImage packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	byte[] cmdStreamData = encodeImageCmdStream(cmdList);
	copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	
	byte[] pngData  = new PngEncoderB(packedImage, false, PngEncoder.FILTER_NONE, 2).pngEncode();
	
	response.setContentType("application/binary");
	OutputStream os = response.getOutputStream();
	os.write(cmdStreamData);
	os.write(pngData);
    }

}
