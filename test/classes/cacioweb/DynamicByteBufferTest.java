package cacioweb;

import java.io.*;

import net.java.openjdk.cacio.servlet.imgformat.*;

import org.junit.*;
import static org.junit.Assert.*;

public class DynamicByteBufferTest {
    DynamicByteBuffer buffer;
    
    @Before
    public void initializeTestBuffer() {
	buffer = new DynamicByteBuffer(512);
	for(int i=0; i < 5000; i++) {
	    buffer.write((byte) (i % 127));
	}
    }
    
    @Test
    public void testDataWritten() throws IOException {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	buffer.writeTo(bos);
	byte[] writtenData = bos.toByteArray();
	
	for(int i=0; i < 5000; i++) {
	    assertEquals(writtenData[i], i % 127);
	}
    }
    
    @Test
    public void testBufferSize() {
	assertEquals(5000, buffer.size());
    }
    
}
