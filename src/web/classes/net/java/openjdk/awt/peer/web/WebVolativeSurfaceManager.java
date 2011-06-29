package net.java.openjdk.awt.peer.web;

import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.SurfaceData;

class WebVolativeSurfaceManager extends VolatileSurfaceManager {

    WebVolativeSurfaceManager(SunVolatileImage image, Object context) {

        super(image, context);
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
