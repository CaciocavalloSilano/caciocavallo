package net.java.openjdk.cacio.servlet;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

public abstract class BinaryCmdStreamEncoder extends CmdStreamEncoder {

    byte[] emptyResponseData;
    
    public BinaryCmdStreamEncoder() {
	super("application/binary");
	
	emptyResponseData = new byte[2];
	emptyResponseData[0] = 0;
	emptyResponseData[1] = 0;
    }
    
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

    @Override
    public void writeEmptyData(OutputStream os) throws IOException {
	os.write(emptyResponseData);
    }
}
