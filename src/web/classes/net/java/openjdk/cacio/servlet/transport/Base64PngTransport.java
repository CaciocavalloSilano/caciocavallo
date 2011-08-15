package net.java.openjdk.cacio.servlet.transport;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import net.java.openjdk.awt.peer.web.*;
import net.java.openjdk.cacio.servlet.base64.*;
import net.java.openjdk.cacio.servlet.imgformat.*;
import sun.misc.*;

public class Base64PngTransport extends Transport {

    private static byte[] emptyResponseData = "0".getBytes();
    BASE64Encoder base64Encoder;

    public Base64PngTransport() {
	super("text/plain");
	
	base64Encoder = new BASE64Encoder();
    }

    protected String encodeImageCmdStream(List<Integer> cmdList) {
	StringBuilder cmdBuilder = new StringBuilder(cmdList.size() * 4);
	cmdBuilder.append(cmdList.size());
	for (int i = 0; i < cmdList.size(); i++) {
	    cmdBuilder.append(':');
	    cmdBuilder.append(cmdList.get(i));
	}

	// Delimeter for following image data
	cmdBuilder.append(':');

	return cmdBuilder.toString();
    }

    @Override
    public void writeEncodedData(OutputStream os, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList)
	    throws IOException {
	String cmdString = encodeImageCmdStream(cmdList);
	os.write(cmdString.getBytes());

	WebRect packedRegionBox = packer.getBoundingBox();
	if (packedRegionBox.getWidth() > 0 && packedRegionBox.getHeight() > 0) {
	    BufferedImage packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	    copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	    byte[] bData = PNGEncoder.getInstance().encode(packedImage, 2);
	    bData = Base64Encoder.encode(bData);
	    os.write(bData);
	}
    }

    @Override
    public void writeEmptyData(OutputStream os) throws IOException {
	os.write(emptyResponseData);
    }
}
