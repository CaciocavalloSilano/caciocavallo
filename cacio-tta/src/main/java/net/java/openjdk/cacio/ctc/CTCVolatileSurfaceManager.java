package net.java.openjdk.cacio.ctc;

import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.SurfaceData;

class CTCVolatileSurfaceManager extends VolatileSurfaceManager {

    protected CTCVolatileSurfaceManager(SunVolatileImage vImg, Object context) {
        super(vImg, context);
    }

    @Override
    protected boolean isAccelerationEnabled() {
        return false;
    }

    @Override
    protected SurfaceData initAcceleratedSurface() {
        return null;
    }

}
