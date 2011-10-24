package net.java.openjdk.cacio.ctc;

import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.SurfaceManagerFactory;

class CTCSurfaceManagerFactory extends SurfaceManagerFactory {

    @Override
    public VolatileSurfaceManager createVolatileManager(SunVolatileImage image,
            Object context) {
        return new CTCVolatileSurfaceManager(image, context);
    }

}
