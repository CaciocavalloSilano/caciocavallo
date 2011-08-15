package net.java.openjdk.cacio.servlet.imgformat;

import java.awt.image.*;
import java.lang.reflect.*;

public class PNGEncoderKeypoint extends PNGEncoder {

    private static Class<?> keypointEncoderCls;
    private static Constructor<?> keypointEncoderConstructor;
    private static Method keypointEncodeMethod;

    public PNGEncoderKeypoint() {
	loadKeypointEncoder();
    }

    private void loadKeypointEncoder() {
	try {
	    keypointEncoderCls = Class.forName("com.keypoint.PngEncoderB");
	    keypointEncoderConstructor = keypointEncoderCls.getConstructor(new Class[] { BufferedImage.class, boolean.class, int.class,
		    int.class });
	    keypointEncodeMethod = keypointEncoderCls.getMethod("pngEncode", new Class[0]);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    protected boolean isAvailable() {
	return keypointEncodeMethod != null;
    }

    @Override
    public byte[] encode(BufferedImage img, int compression) {
	try {
	    Object encoderInstance = keypointEncoderConstructor.newInstance(new Object[] { img, Boolean.FALSE, Integer.valueOf(0), compression });
	    return (byte[]) keypointEncodeMethod.invoke(encoderInstance, new Object[0]);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	return null;
    }
}
