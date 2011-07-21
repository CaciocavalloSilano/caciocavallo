package net.java.openjdk.cacio.servlet;

import java.io.*;
import java.util.*;

public class DynamicByteBuffer {
    final static int BUFFER_SIZE = 8192;
    
    ArrayList<byte[]> bufferList = new ArrayList<byte[]>();
    
    int curBufferPos = 0;
    byte[] curBuffer;
    
    public DynamicByteBuffer() {
	addBuffer();
    }
    
    public final void write(byte b) {
	if(curBufferPos == curBuffer.length) {
	    addBuffer();
	}
	
	curBuffer[curBufferPos] = b;
	curBufferPos++;
    }
    
    private void addBuffer() {
	curBuffer = new byte[BUFFER_SIZE];
	curBufferPos = 0;
	bufferList.add(curBuffer);
    }
    
    public int size() {
	return (bufferList.size() - 1) * BUFFER_SIZE + curBufferPos;
    }
    
    public void writeTo(OutputStream os) throws IOException {
	int size = size();
	for(int i=0; i < bufferList.size(); i++) {
	    int curBuffLength = Math.min(size - BUFFER_SIZE*i, BUFFER_SIZE);
	    byte[] curBuff = bufferList.get(i);
	    os.write(curBuff, 0, curBuffLength);
	}
    }
    
    
}
