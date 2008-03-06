package gnu.java.awt.peer.x;

import gnu.x11.Pixmap;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;

import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.SurfaceData;

public class EscherVolatileSurfaceManager
  extends VolatileSurfaceManager
{

  public EscherVolatileSurfaceManager(SunVolatileImage vImg, Object context)
  {
    super(vImg, context);
  }

  @Override
  protected boolean isAccelerationEnabled()
  {
    return vImg.getTransparency() == Transparency.OPAQUE;
  }

  @Override
  protected SurfaceData initAcceleratedSurface()
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice gd = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    XGraphicsDevice xgd = (XGraphicsDevice) gd;
    XGraphicsConfiguration xgc = (XGraphicsConfiguration) gc;
    Pixmap pm = new Pixmap(xgd.getDisplay(), vImg.getWidth(),
                           vImg.getHeight());
    return new XDrawableSurfaceData(xgc, pm, XDrawableSurfaceData.EscherIntRgb,
                                    xgc.getColorModel());
  }


}
