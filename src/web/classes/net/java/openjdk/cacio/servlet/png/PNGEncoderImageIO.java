package net.java.openjdk.cacio.servlet.png;

import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;

import javax.imageio.*;

public class PNGEncoderImageIO extends PNGEncoder {    
    @Override
    public byte[] encode(BufferedImage img, int compression) {
	ByteArrayOutputStream bos = new ByteArrayOutputStream((img.getWidth() * img.getHeight()) / 2);

	try {
	    ImageIO.write(img, "PNG", bos);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	
	return bos.toByteArray();
    }

}
