package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import net.java.openjdk.awt.peer.web.*;

import biz.source_code.base64Coder.*;

import com.keypoint.*;

public class Base64CmdStreamEncoder extends CmdStreamEncoder {

    protected String encodeImageCmdStream(List<Integer> cmdList) {
	StringBuilder cmdBuilder = new StringBuilder(cmdList.size() * 4);
	cmdBuilder.append(cmdList.size());
	for(int i=0; i < cmdList.size(); i++) {
	    cmdBuilder.append(':');
	    cmdBuilder.append(cmdList.get(i));
	}
	
	//Delimeter for following image data
	cmdBuilder.append(':');
	
	return cmdBuilder.toString();
    }
    
    public byte[] getEncodedData(List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList) {
	DamageRect packedRegionBox = packer.getBoundingBox();
	
	BufferedImage packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	String cmdString = ""; //encodeImageCmdStream(cmdData);
	copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	
	byte[] bData = null;
	try {
	    bData = new PngEncoderB(packedImage, false, PngEncoder.FILTER_NONE, 2).pngEncode();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	byte[] data = Base64Coder.encode(bData);
	byte[] cmdStringBytes = cmdString.getBytes();
	
	//TODO: Optimize interface, so we do not have to copy data arround like this
	ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length + cmdStringBytes.length);
	try {
	    bos.write(cmdStringBytes);
	    bos.write(data);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	return bos.toByteArray();
    }
}
