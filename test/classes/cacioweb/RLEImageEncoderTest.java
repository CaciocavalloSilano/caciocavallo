package cacioweb;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import net.java.openjdk.cacio.servlet.imgformat.*;

import org.junit.*;
import static org.junit.Assert.*;

public class RLEImageEncoderTest {

    BufferedImage srcImg;

    @Before
    public void setupTestData() {
	srcImg = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
	Graphics2D g2d = (Graphics2D) srcImg.getGraphics();
	g2d.setPaint(new GradientPaint(new Point(0, 0), Color.YELLOW, new Point(0, 256), Color.RED));
	g2d.fillRect(0, 0, 256, 256);
    }

    @Test
    public void testEncodedData() throws IOException {
	int width = 150;
	int height = 50;
	int srcXOffset = 10;
	int srcYOffset = 100;
	
	RLEImageEncoder encoder = new RLEImageEncoder();

	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	encoder.encodeImageToStream(srcImg, srcXOffset, srcYOffset, srcXOffset + width, srcYOffset + height, bos);
	byte[] encodedData = bos.toByteArray();

	DataInputStream din = new DataInputStream(new ByteArrayInputStream(encodedData));
	assertEquals(width, din.readShort());
	assertEquals(height, din.readShort());

	BufferedImage decodedImage = decodeRle(encodedData, din.readInt(), width, height);
	
	
	for(int y = 0; y < height; y++) {
		for(int x = 0; x < width; x++) {
		    int origPixel = srcImg.getRGB(x+srcXOffset, y+srcYOffset);
		    int decodedPixel = decodedImage.getRGB(x, y);
		    assertEquals(origPixel, decodedPixel);
		}
	}
    }

    private BufferedImage decodeRle(byte[] rle, int runDataLength, int w, int h) {
	BufferedImage bImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

	int pixelDataOffset = 8 + runDataLength;
	int lastRed = 0, lastGreen = 0, lastBlue = 0;
	int pixelPos = 0;
	for (int i = 0; i < runDataLength; i++) {
	    int cmd = rle[i + 8];
	    int length = cmd & 127;

	    if (cmd > 0) {
		// runs
		while (length-- > 0) {
		    bImg.setRGB(pixelPos % w, pixelPos / w, 255 << 24 | lastRed << 16 | lastGreen << 8 | lastBlue);
		    pixelPos++;
		}
	    } else {
		// no-runs
		while (length-- > 0) {
		    lastRed = rle[pixelDataOffset++];
		    lastGreen = rle[pixelDataOffset++];
		    lastBlue = rle[pixelDataOffset++];

		    bImg.setRGB(pixelPos % w, pixelPos / w, 255 << 24 | lastRed << 16 | lastGreen << 8 | lastBlue);
		    pixelPos++;
		}
	    }
	}

	return bImg;
    }
}
