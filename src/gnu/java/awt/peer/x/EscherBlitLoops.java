package gnu.java.awt.peer.x;

import gnu.x11.Drawable;
import gnu.x11.GC;
import gnu.x11.image.ZPixmap;

import java.awt.Composite;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import sun.awt.SunToolkit;
import sun.awt.image.BufImgSurfaceData;
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
                            XDrawableSurfaceData.EscherIntRgb, true),
        new EscherBlitLoops(SurfaceType.Any, SurfaceType.Any, true)
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
    if (src instanceof BufImgSurfaceData)
      blitBufImg((BufImgSurfaceData) src, dst, comp, clip, sx, sy, dx, dy, w, h);
    else
      {
        XDrawableSurfaceData sxdsd = (XDrawableSurfaceData) src;
        XDrawableSurfaceData dxdsd = (XDrawableSurfaceData) dst;
        GC gc = dxdsd.getBlitGC(clip);
        Drawable d = dxdsd.getDrawable();
        Drawable s = sxdsd.getDrawable();
        d.copy_area(s, gc, sx, sy, w, h, dx, dy);
        d.display.flush();
      }
    SunToolkit.awtUnlock();
  }

  private void  blitBufImg(BufImgSurfaceData src, SurfaceData dst,
                           Composite comp, Region clip, int sx, int sy, int dx,
                           int dy, int w, int h)
  {
    // TODO: Implement transparency.
    BufferedImage bufImg = (BufferedImage) src.getDestination();
    XDrawableSurfaceData dxdsd = (XDrawableSurfaceData) dst;
    Drawable d = dxdsd.getDrawable();
    GC gc = dxdsd.getBlitGC(clip);
    int transparency = bufImg.getTransparency();
    if (transparency == Transparency.OPAQUE)
      {
        ZPixmap pm = new ZPixmap(gc.display, w, h);
        for (int y = sy; y < sy + h; y++)
          {
            for (int x = sx; x < sx + w; x++)
              {
                int rgb = bufImg.getRGB(x, y);
                pm.set(x, y, rgb);
              }
          }
        d.put_image(gc, pm, dx, dy);
      }
    else
      {
        ZPixmap zpixmap = (ZPixmap) d.image(dx, dy, w, h, 0xffffffff,
                                           gnu.x11.image.Image.Format.ZPIXMAP);
        for (int yy = 0; yy < h; yy++)
          {
            for (int xx = 0; xx < w; xx++)
              {
                int rgb = bufImg.getRGB(xx + sx, yy + sy);
                int alpha = 0xff & (rgb >> 24);
                if (alpha == 0)
                  {
                    // Completely translucent.
                    rgb = zpixmap.get_red(xx, yy) << 16
                          | zpixmap.get_green(xx, yy) << 8
                          | zpixmap.get_blue(xx, yy);
                  }
                else if (alpha < 255)
                  {
                    // Composite pixels.
                    int red = 0xff & (rgb >> 16);
                    red = red * alpha
                             + (255 - alpha) * zpixmap.get_red(xx, yy);
                    red = red / 255;
                    int green = 0xff & (rgb >> 8);
                    green = green * alpha
                           + (255 - alpha) * zpixmap.get_green(xx, yy);
                    green = green / 255;
                    int blue = 0xff & rgb;
                    blue = blue * alpha
                            + (255 - alpha) * zpixmap.get_blue(xx, yy);
                    blue = blue / 255;
                    rgb = red << 16 | green << 8 | blue;
                  }
                // else keep rgb value from source image.

                zpixmap.set(xx, yy, rgb);
              }
          }
        d.put_image(gc, zpixmap, dx, dy);
      }
  }
}
