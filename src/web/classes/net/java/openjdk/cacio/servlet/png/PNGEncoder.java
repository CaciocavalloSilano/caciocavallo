package net.java.openjdk.cacio.servlet.png;

import java.awt.image.*;

public abstract class PNGEncoder {

    private static PNGEncoder instance;
    
    public static synchronized PNGEncoder getInstance() {
	if(instance == null) {
	    instance = loadInstance(PNGEncoder.class.getName().replace("PNGEncoder", "PNGEncoderImageIO"));
	}
	
	return instance;
    }
    
    private static PNGEncoder loadInstance(String className) {
	try {
	    Class cls = Class.forName(className);
	    return (PNGEncoder) cls.newInstance();
	}catch(Exception ex) {
	    return null;
	}
    }
    
    public abstract byte[] encode(BufferedImage img, int compression);
}
