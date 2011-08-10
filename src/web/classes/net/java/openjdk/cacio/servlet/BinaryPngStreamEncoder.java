package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;
import net.java.openjdk.cacio.servlet.png.*;

import com.keypoint.*;

public class BinaryPngStreamEncoder extends BinaryCmdStreamEncoder {

    @Override
    public void writeEncodedData(OutputStream os, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList)
	    throws IOException {

	byte[] cmdStreamData = encodeImageCmdStream(cmdList);
	os.write(cmdStreamData);

	DamageRect packedRegionBox = packer.getBoundingBox();
	if (packedRegionBox.getWidth() > 0 && packedRegionBox.getHeight() > 0) {
	    BufferedImage packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	    copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	    byte[] pngData = PNGEncoder.getInstance().encode(packedImage, 2);
	    os.write(pngData);
	}
    }
}
