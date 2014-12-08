package net.java.openjdk.cacio.directfb;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.util.Locale;

public class DirectFBGraphicsEnvironment extends GraphicsEnvironment {

    static {
        NarSystem.loadLibrary();
    }

    private long nativePtr;

    private native long createDirectFB();

    public DirectFBGraphicsEnvironment() {
        nativePtr = createDirectFB();
    }

    @Override
    public GraphicsDevice[] getScreenDevices() throws HeadlessException {
        throw new InternalError("NYI");
    }

    @Override
    public GraphicsDevice getDefaultScreenDevice() throws HeadlessException {
        throw new InternalError("NYI");
    }

    @Override
    public Graphics2D createGraphics(BufferedImage img) {
        throw new InternalError("NYI");
    }

    @Override
    public Font[] getAllFonts() {
        throw new InternalError("NYI");
    }

    @Override
    public String[] getAvailableFontFamilyNames() {
        throw new InternalError("NYI");
    }

    @Override
    public String[] getAvailableFontFamilyNames(Locale l) {
        throw new InternalError("NYI");
    }

}
