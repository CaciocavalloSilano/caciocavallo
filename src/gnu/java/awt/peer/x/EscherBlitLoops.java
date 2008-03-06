package gnu.java.awt.peer.x;

import gnu.x11.Drawable;
import gnu.x11.GC;

import java.awt.Composite;

import sun.awt.SunToolkit;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

class EscherBlitLoops
  extends Blit
{

  static void register()
  {
      GraphicsPrimitive[] primitives =
      {
//        new EscherBlitLoops(SurfaceType.IntArgb,
//                            XDrawableSurfaceData.EscherIntRgb, false),
        new EscherBlitLoops(XDrawableSurfaceData.EscherIntRgb,
                            XDrawableSurfaceData.EscherIntRgb, true)
      };
      GraphicsPrimitiveMgr.register(primitives);
  }

  private EscherBlitLoops(SurfaceType srcType, SurfaceType dstType,
                          boolean over)
  {
    super(srcType,
          over ? CompositeType.SrcOverNoEa : CompositeType.SrcNoEa,
          dstType);
  }

  public void Blit(SurfaceData src, SurfaceData dst,
                   Composite comp, Region clip,
                   int sx, int sy,
                   int dx, int dy,
                   int w, int h)
  {
    SunToolkit.awtLock();
    XDrawableSurfaceData sxdsd = (XDrawableSurfaceData) src;
    XDrawableSurfaceData dxdsd = (XDrawableSurfaceData) dst;
    GC gc = dxdsd.getBlitGC(clip);
    Drawable d = dxdsd.getDrawable();
    Drawable s = sxdsd.getDrawable();
    d.copy_area(s, gc, sx, sy, w, h, dx, dy);
    d.display.flush();
    SunToolkit.awtUnlock();
  }
}
