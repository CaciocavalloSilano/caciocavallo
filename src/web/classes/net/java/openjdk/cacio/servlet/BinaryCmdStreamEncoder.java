package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import net.java.openjdk.awt.peer.web.*;
import biz.source_code.base64Coder.*;

import com.keypoint.*;

public class BinaryCmdStreamEncoder extends CmdStreamEncoder {

    protected byte[] encodeImageCmdStream(List<Integer> cmdList) {
	
	ByteArrayOutputStream bos = new ByteArrayOutputStream(cmdList.size()*2 + 2);
	DataOutputStream dos = new DataOutputStream(bos);
	
	try {
	    dos.writeShort(cmdList.size());
	    for(int i=0; i < cmdList.size(); i++) {
	        dos.writeShort(cmdList.get(i));
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return bos.toByteArray();
    }
    
    public byte[] getEncodedData(List<ScreenUpdate> pendingUpdateList, TreeImagePacker packer, List<Integer> cmdList) {
	DamageRect packedRegionBox = packer.getBoundingBox();
	
	BufferedImage packedImage = new BufferedImage(packedRegionBox.getWidth(), packedRegionBox.getHeight(), BufferedImage.TYPE_INT_RGB);
	byte[] cmdStreamData = encodeImageCmdStream(cmdList);
	copyUpdatesToPackedImage(pendingUpdateList, packedImage, 0);
	
	byte[] bData = null;
	try {
	    bData = new PngEncoderB(packedImage, false, PngEncoder.FILTER_NONE, 2).pngEncode();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	//TODO: Optimize interface, so we do not have to copy data arround like this
	ByteArrayOutputStream bos = new ByteArrayOutputStream(bData.length + cmdStreamData.length);
	try {
	    bos.write(cmdStreamData);
	    bos.write(bData);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	return bos.toByteArray();
    }

}
