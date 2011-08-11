package net.java.openjdk.cacio.servlet.png;

import java.awt.image.*;

public abstract class PNGEncoder {

    private static PNGEncoder instance;
    
    public static synchronized PNGEncoder getInstance() {
	if(instance == null) {
	    PNGEncoderKeypoint keypointEncoder = new PNGEncoderKeypoint();
	    if(keypointEncoder.isAvailable()) {
		instance = keypointEncoder;
	    }else {
		instance = new PNGEncoderImageIO();
		System.out.println("Keypoint PNG-Encoder not found, falling back to ImageIO.");
	    }
	    
	}
	
	return instance;
    }
    
  
    
    public abstract byte[] encode(BufferedImage img, int compression);
}
