package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;

public class BinaryRLEStreamEncoder extends BinaryCmdStreamEncoder {
    
    @Override
    public void writeEnocdedData(OutputStream os, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList) throws IOException {
	DamageRect packedRegionBox = packer.getBoundingBox();
	
	long start = System.currentTimeMillis();
	BufferedImage packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	byte[] cmdStreamData = encodeImageCmdStream(cmdList);
	copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	long end = System.currentTimeMillis();
	System.out.println("Packing took: "+(end-start));
	
	os.write(cmdStreamData);
	new RLEImageEncoder().encodeImageToStream(packedImage, packedImage.getWidth(), packedImage.getHeight(), os);
    }

}
