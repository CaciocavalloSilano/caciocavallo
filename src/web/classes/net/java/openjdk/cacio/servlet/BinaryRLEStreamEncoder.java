package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;

public class BinaryRLEStreamEncoder extends BinaryCmdStreamEncoder {

    @Override
    public void writeEnocdedData(OutputStream os, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList)
	    throws IOException {
	DamageRect packedRegionBox = packer.getBoundingBox();

	byte[] cmdStreamData = encodeImageCmdStream(cmdList);
	os.write(cmdStreamData);

	// TODO: Optimize case where only 1 BlitScreenUpdate is pending
	long start = System.currentTimeMillis();
	if (pendingUpdateList.size() == 1 && pendingUpdateList.get(0) instanceof BlitScreenUpdate) {
	    BlitScreenUpdate update = (BlitScreenUpdate) pendingUpdateList.get(0);
	    new RLEImageEncoder().encodeImageToStream(update.getImage(), update.getSrcX(), update.getSrcY(), update.getSrcX() + update.getUpdateArea().getWidth(), update.getSrcY() + update.getUpdateArea().getHeight(), os);
	    System.out.println("Took fast path");
	} else {
	    BufferedImage packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	    copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	    new RLEImageEncoder().encodeImageToStream(packedImage, 0, 0, packedImage.getWidth(), packedImage.getHeight(), os);
	}

	long end = System.currentTimeMillis();
	System.out.println("Packing took: " + (end - start));
    }
}
