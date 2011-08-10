package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import sun.misc.*;

import net.java.openjdk.awt.peer.web.*;
import net.java.openjdk.cacio.servlet.base64.*;
import net.java.openjdk.cacio.servlet.png.*;

import biz.source_code.base64Coder.*;

import com.keypoint.*;

public class Base64CmdStreamEncoder extends CmdStreamEncoder {

    BASE64Encoder base64Encoder;
    
    public Base64CmdStreamEncoder() {
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

	DamageRect packedRegionBox = packer.getBoundingBox();
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
	os.write("0".getBytes());
    }
}
