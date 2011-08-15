package net.java.openjdk.cacio.servlet.transport;

import java.io.*;
import java.util.*;

public abstract class BinaryTransport extends Transport {

    byte[] emptyResponseData;
    
    public BinaryTransport() {
	super("application/binary");
	
	emptyResponseData = new byte[2];
	emptyResponseData[0] = 0;
	emptyResponseData[1] = 0;
    }
    
    protected byte[] encodeImageCmdStream(List<Integer> cmdList) {
	ByteArrayOutputStream bos = new ByteArrayOutputStream(cmdList.size()*2 + 2);
	
	writeJSShort(bos, cmdList.size());
	for(int value : cmdList) {
	    writeJSShort(bos, value);
	}

	return bos.toByteArray();
    }
    
    protected void writeJSShort(ByteArrayOutputStream bos, int value) {
	int sign = value >= 0 ? 0 : 1;
	int absValue = Math.abs(value);
	
	int highByte = ((absValue & 32512) >> 8) + (sign << 7);
	int lowByte = absValue & 0x000000FF;

	bos.write(highByte);
	bos.write(lowByte);
    }

    @Override
    public void writeEmptyData(OutputStream os) throws IOException {
	os.write(emptyResponseData);
    }
}
