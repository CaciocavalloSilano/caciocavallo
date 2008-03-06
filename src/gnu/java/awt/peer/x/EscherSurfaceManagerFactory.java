package gnu.java.awt.peer.x;

import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.SurfaceManagerFactory;

class EscherSurfaceManagerFactory
    extends SurfaceManagerFactory
{

  @Override
  public VolatileSurfaceManager createVolatileManager(SunVolatileImage image,
                                                      Object context)
  {
    return new EscherVolatileSurfaceManager(image, context);
  }

}
