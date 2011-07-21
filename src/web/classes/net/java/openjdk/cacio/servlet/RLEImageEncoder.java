package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;


public class RLEImageEncoder {
    DynamicByteBuffer runBuffer = new DynamicByteBuffer();
    DynamicByteBuffer dataBuffer = new DynamicByteBuffer();

    public void encodeImageToStream(BufferedImage img, int w, int h, OutputStream os) throws IOException {
	DataOutputStream dos = new DataOutputStream(os);
	dos.writeShort(w);
	dos.writeShort(h);
	
	encodeImageData(img, w, h);
	dos.writeInt(runBuffer.size());
	
	runBuffer.writeTo(os);
	dataBuffer.writeTo(os);
    }

    public void encodeImageData(BufferedImage img, int w, int h) {
	int[] imgData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
	int imgStride = img.getWidth(); // TODO: Thats no stride

	int lastPixelValue = -1;
	int runCount = 0;
	int nonRunCount = 0;

	for (int y = 0; y < h; y++) {
	    int lineStartPos = imgStride * y;

	    for (int x = 0; x < w; x++) {
		int pixelValue = imgData[lineStartPos + x] & 0x00FFFFFF;

		// We have a run, handle it
		if (lastPixelValue == pixelValue) {

		    // Terminate existing no-run
		    if (nonRunCount > 0) {
			endNoRun(runBuffer, nonRunCount);
			nonRunCount = 0;
		    }

		    if (runCount < 127) {
			// Fast path for long runs
			for (; runCount < 127 && x < w && ((imgData[lineStartPos + x] & 0x00FFFFFF)) == lastPixelValue; x++) {
			    runCount++;
			}
			x--; // We aborted, so we have to look at this pixel
			     // again at the next full iteration
		    } else {
			endRun(runBuffer, runCount);
			runCount = 0;
		    }
		} else {
		    // Terminate existing run
		    if (runCount > 0) {
			endRun(runBuffer, runCount);
			runCount = 0;
		    }

		    // We have a no-run
		    if (nonRunCount < 127) {
			nonRunCount++;
		    } else {
			endNoRun(runBuffer, nonRunCount);
			nonRunCount = 0;
		    }

		    writePixel(dataBuffer, pixelValue);
		}

		lastPixelValue = pixelValue;
	    }
	}

	if (runCount > 0) {
	    endRun(dataBuffer, runCount);
	}

	if (nonRunCount > 0) {
	    endNoRun(runBuffer, nonRunCount);
	}
    }

    static int optBreaker;

    protected void writePixel(DynamicByteBuffer dataStream, int pixel) {
	byte r = (byte) ((pixel & 0x00FF0000) >> 16);
	byte g = (byte) ((pixel & 0x0000FF00) >> 8);
	byte b = (byte) (pixel & 0x000000FF);

	dataStream.write(r);
	dataStream.write(g);
	dataStream.write(b);
    }


    private void endNoRun(DynamicByteBuffer runStream, int noRunCnt) {
	runStream.write((byte) (128 + noRunCnt));
    }

    private void endRun(DynamicByteBuffer runStream, int runCnt) {
	runStream.write((byte) runCnt);
    }

}
