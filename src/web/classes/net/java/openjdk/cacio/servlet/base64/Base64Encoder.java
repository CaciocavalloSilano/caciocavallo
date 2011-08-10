package net.java.openjdk.cacio.servlet.base64;

public class Base64Encoder {

    private final static byte padByte = (byte) '=';

    private final static byte[] map = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I',
	    (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T',
	    (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e',
	    (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p',
	    (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0',
	    (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/' };

    public static byte[] encode(byte[] in) {
	int dataLength = (in.length * 4 + 2) / 3;
	byte[] out = new byte[((in.length + 2) / 3) * 4]; // length including
							  // pad-bytes
	int i = 0, o = 0;
	while (i < in.length) {
	    // Read 3 input bytes
	    int i1 = in[i++] & 0xff;
	    int i2;
	    int i3;

	    if (i +1 < in.length) {
		i2 = in[i++] & 0xff;
		i3 = in[i++] & 0xff;
	    } else {
		i2 = 0;
		i3 = 0;
		
		if(i < in.length) {
		    i2 = in[i++] & 0xff;
		}
		if(i < in.length) {
		    i3 = in[i++] & 0xff;
		}
	    }

	    // Store the 3 input bytes in 4x6-bit output values.
	    int o1 = i1 >>> 2;
	    int o2 = ((i1 & 3) << 4) | (i2 >>> 4);
	    int o3 = ((i2 & 15) << 2) | (i3 >>> 6);
	    int o4 = i3 & 63;

	    //Look up characters, write to output and pad if requred
	    out[o++] = map[o1];
	    out[o++] = map[o2];

	    if (o + 1 < dataLength) {
		out[o++] = map[o3];
		out[o++] = map[o4];
	    } else {
		out[o] = padByte;
		out[o + 1] = padByte;
		
		if(o < dataLength) {
		    out[o++] = map[o3];
		}
		
		if(o < dataLength) {
		    out[o++] = map[o4];
		}
	    }
	}
	
	return out;
    }
}
