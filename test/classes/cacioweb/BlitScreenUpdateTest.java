package cacioweb;

import java.awt.image.*;


import net.java.openjdk.awt.peer.web.*;

import org.junit.*;
import static org.junit.Assert.*;

public class BlitScreenUpdateTest {

    @Test
    public void testSourceEvacuation() {
	BufferedImage origSrc = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
	BlitScreenUpdate bs = new BlitScreenUpdate(10, 10, 10, 10, 10, 10, origSrc);
	bs.evacuate();
	
	//Evacuated BlitScreenUpdate doesn't use the original source image anymore
	BufferedImage newSrc = bs.getImage();
	assertTrue(origSrc != newSrc);
	
	//So, src coordinates are adopted
	assertEquals(0, bs.getSrcX());
	assertEquals(0, bs.getSrcY());
    }
    
    @Test
    public void testNoDoubleEvacuation() {
	BufferedImage origSrc = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
	BlitScreenUpdate bs = new BlitScreenUpdate(10, 10, 10, 10, 10, 10, origSrc);
	bs.evacuate();
	BufferedImage src1 = bs.getImage();
	
	//If image is already evacuated, second call is a no-op
	bs.evacuate();
	BufferedImage src2 = bs.getImage();
	
	assertEquals(src1, src2);
    }
}
