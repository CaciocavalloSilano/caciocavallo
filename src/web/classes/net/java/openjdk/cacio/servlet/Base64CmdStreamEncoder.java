package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import net.java.openjdk.awt.peer.web.*;

import biz.source_code.base64Coder.*;

import com.keypoint.*;

public class Base64CmdStreamEncoder extends CmdStreamEncoder {

    public Base64CmdStreamEncoder() {
	super("text/plain");
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
    public void writeEnocdedData(OutputStream os, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList)
	    throws IOException {
	String cmdString = encodeImageCmdStream(cmdList);
	os.write(cmdString.getBytes());

	DamageRect packedRegionBox = packer.getBoundingBox();
	if (packedRegionBox.getWidth() > 0 && packedRegionBox.getHeight() > 0) {
	    BufferedImage packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	    copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	    byte[] bData = new PngEncoderB(packedImage, false, PngEncoder.FILTER_NONE, 2).pngEncode();
	    bData = Base64Coder.encode(bData);
	    os.write(bData);
	}
    }

    @Override
    public void writeEmptyData(OutputStream os) throws IOException {
	os.write("0".getBytes());
    }
}
