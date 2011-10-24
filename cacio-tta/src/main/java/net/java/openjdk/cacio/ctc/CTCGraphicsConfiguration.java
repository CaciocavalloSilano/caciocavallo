package net.java.openjdk.cacio.ctc;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;

import sun.awt.peer.cacio.managed.FullScreenWindowFactory;

public class CTCGraphicsConfiguration extends GraphicsConfiguration {

    private CTCGraphicsDevice device;

    CTCGraphicsConfiguration(CTCGraphicsDevice dev) {
        device = dev;
    }

    @Override
    public GraphicsDevice getDevice() {
        return device;
    }

    @Override
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    @Override
    public ColorModel getColorModel(int transparency) {
        return ColorModel.getRGBdefault();
    }

    @Override
    public AffineTransform getDefaultTransform() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AffineTransform getNormalizingTransform() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Rectangle getBounds() {
        Dimension d = FullScreenWindowFactory.getScreenDimension();
        return new Rectangle(0, 0, d.width, d.height);
    }

}
