package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.servlet.http.*;

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
    
    @Override
    public void writeEnocdedData(HttpServletResponse response, List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList) throws IOException {
	DamageRect packedRegionBox = packer.getBoundingBox();
	
	BufferedImage packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	String cmdString = encodeImageCmdStream(cmdList);
	copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	
	byte[] bData = new PngEncoderB(packedImage, false, PngEncoder.FILTER_NONE, 2).pngEncode();
	bData = Base64Coder.encode(bData);
	
	OutputStream os = response.getOutputStream();
	response.setContentType("text/plain");
	os.write(cmdString.getBytes());
	os.write(bData);
    }

    @Override
    public void writeEmptyData(HttpServletResponse response) throws IOException {
	response.setContentType("text/plain");
	response.getOutputStream().write("0".getBytes());
    }
}
