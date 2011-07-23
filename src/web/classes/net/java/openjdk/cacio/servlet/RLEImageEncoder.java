package net.java.openjdk.cacio.servlet;

import java.awt.image.*;
import java.io.*;

public class RLEImageEncoder {

    public void encodeImageToStream(BufferedImage img, int x1, int y1, int x2, int y2, OutputStream os) throws IOException {
	DynamicByteBuffer runBuffer = new DynamicByteBuffer();
	DynamicByteBuffer dataBuffer = new DynamicByteBuffer();

	DataOutputStream dos = new DataOutputStream(os);
	int w = x2 - x1;
	int h = y2 - y1;
	dos.writeShort(w);
	dos.writeShort(h);

	encodeImageData(img, x1, y1, x2, y2, runBuffer, dataBuffer);
	dos.writeInt(runBuffer.size());

	runBuffer.writeTo(os);
	dataBuffer.writeTo(os);
    }

    private void encodeImageData(BufferedImage img, int x1, int y1, int x2, int y2, DynamicByteBuffer runBuffer, DynamicByteBuffer dataBuffer) {
	int[] imgData = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
	int imgStride = ((SinglePixelPackedSampleModel) img.getSampleModel()).getScanlineStride();

	int lastPixelValue = -1;
	int runCount = 0;
	int nonRunCount = 0;

	for (int y = y1; y < y2; y++) {
	    int lineStartPos = imgStride * y;

	    for (int x = x1; x < x2; x++) {
		int pixelValue = imgData[lineStartPos + x] & 0x00FFFFFF;

		// We have a run, handle it
		if (lastPixelValue == pixelValue) {

		    // Terminate existing no-run
		    if (nonRunCount > 0) {
			endNoRun(runBuffer, nonRunCount);
			nonRunCount = 0;
		    }

		    if (runCount < 127) {
			// Fast path for runs
			for (; runCount < 127 && x < x2 && ((imgData[lineStartPos + x] & 0x00FFFFFF)) == lastPixelValue; x++) {
			    runCount++;
			}
			x--; // We aborted for some reason, so we have to look
			     // at this pixel again at the next full iteration
		    } else {
			endRun(runBuffer, runCount);
			runCount = 1;
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
			nonRunCount = 1;
		    }

		    writePixel(dataBuffer, pixelValue);
		}

		lastPixelValue = pixelValue;
	    }
	}

	if (runCount > 0) {
	    endRun(runBuffer, runCount);
	}

	if (nonRunCount > 0) {
	    endNoRun(runBuffer, nonRunCount);
	}
    }

    private void writePixel(DynamicByteBuffer dataStream, int pixel) {
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
