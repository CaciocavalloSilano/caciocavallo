/*
 * Copyright (c) 2011, Clemens Eisserer, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package net.java.openjdk.cacio.servlet.imgformat;

import java.awt.image.*;
import java.io.*;

/**
 * Run-Length encoder for images.
 * 
 * @author Clemens Eisserer <linuxhippy@gmail.com>
 */
public class RLEImageEncoder {

    byte[] sizeInfoBuffer;
    DynamicByteBuffer runBuffer;
    DynamicByteBuffer dataBuffer;

    /**
     * RLE encodes the specified area of the image between x1/y1 and x2/y2 of
     * the passed image and writes the resulting data to the OutputStream.
     * Preceeding the image-data the image-dimensions are written as a 2-byte
     * integer value.
     * 
     * @param img
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param os
     * @throws IOException
     */
    public void encodeImageToStream(BufferedImage img, int x1, int y1, int x2, int y2) {
	int w = x2 - x1;
	int h = y2 - y1;
	int area = w * h;

	runBuffer = new DynamicByteBuffer(area < 10000 ? 256 : 1024);
	dataBuffer = new DynamicByteBuffer(area < 10000 ? 512 : 4096);

	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	DataOutputStream dos = new DataOutputStream(bos);
	try {
	    dos.writeShort(w);
	    dos.writeShort(h);

	    encodeImageData(img, x1, y1, x2, y2, runBuffer, dataBuffer);
	    dos.writeInt(runBuffer.size());

	    sizeInfoBuffer = bos.toByteArray();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void writeTo(OutputStream os) throws IOException {
	os.write(sizeInfoBuffer);
	runBuffer.writeTo(os);
	dataBuffer.writeTo(os);
    }

    /**
     * RLE encides the specified area of the BufferedImage.
     * 
     * Run-Length and image-data information is stored seperatly in two
     * DataBuffers. Each byte in the rle-buffer specifies either a "run"
     * (runByte & 128 == 0) or a non-run otherwise.
     * 
     * For a run, the last pixel-value is repeated (runByte & 127) times. For a
     * no-run, no runs are expected for the next (runByte & 127) times, and
     * pixel-data can be fetched from the pixel-data buffer without checking
     * when decoding.
     * 
     * @param img
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param runBuffer
     *            run-length-data is saved to this DataBuffer
     * @param dataBuffer
     *            pixel-data is saved to this DataBuffer.
     */
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
			int startIndex = lineStartPos + x;
			int maxIndex = startIndex + Math.min(127 - runCount, x2 - x);
			int i = startIndex;
			while (i < maxIndex && (imgData[i] & 0x00FFFFFF) == lastPixelValue) {
			    i++;
			}

			int runs = (i - startIndex);
			runCount += runs;
			x += runs - 1; // We aborted, so we have to look at this
				       // pixel again at the next full iteration
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

    /**
     * Appends the RGB pixel data of the specified pixel to the
     * DynamicByteBuffer passed.
     * 
     * @param dataStream
     * @param pixel
     */
    private void writePixel(DynamicByteBuffer dataStream, int pixel) {
	byte r = (byte) ((pixel & 0x00FF0000) >> 16);
	byte g = (byte) ((pixel & 0x0000FF00) >> 8);
	byte b = (byte) (pixel & 0x000000FF);

	dataStream.write(r);
	dataStream.write(g);
	dataStream.write(b);
    }

    /**
     * Encodes the end of a no-run into a run-byte and appends it to the buffer.
     * 
     * @param runStream
     * @param noRunCnt
     */
    private void endNoRun(DynamicByteBuffer runStream, int noRunCnt) {
	runStream.write((byte) (128 + noRunCnt));
    }

    /**
     * Encodes the end of a run into a run-byte and appends it to the buffer.
     * 
     * @param runStream
     * @param runCnt
     */
    private void endRun(DynamicByteBuffer runStream, int runCnt) {
	runStream.write((byte) runCnt);
    }
}
