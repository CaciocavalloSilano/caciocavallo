/* EscherBlitLoops.java
   Copyright (C) 2008 Mario Torre and Roman Kennke

This file is part of the Caciocavallo project.

Caciocavallo is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

Caciocavallo is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with Caciocavallo; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package gnu.java.awt.peer.x;

import gnu.x11.Drawable;
import gnu.x11.GC;
import gnu.x11.image.ZPixmap;

import java.awt.Composite;
import java.awt.Rectangle;
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
        new EscherBlitLoops(SurfaceType.Any, XDrawableSurfaceData.EscherIntRgb, true),
        new EscherBlitLoops(SurfaceType.Any, XDrawableSurfaceData.EscherIntRgb, false)
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
    
    BufferedImage bufImg = (BufferedImage) src.getDestination();
    XDrawableSurfaceData dxdsd = (XDrawableSurfaceData) dst;
    Drawable d = dxdsd.getDrawable();
    GC gc = dxdsd.getBlitGC(clip);

    // Do the clipping dance, to avoid X errors.
    //System.err.println("unclipped: " + sx + ", " + sy + " -> " + dx + ", " + dy + " x " + w + ", " + h);
    Rectangle dr = new Rectangle(dx, dy, w, h);
    Rectangle dc = dr.intersection(new Rectangle(0, 0, d.width, d.height));
    sx = sx + (dc.x - dx);
    sy = sy + (dc.y - dy);
    dx = dc.x;
    dy = dc.y;
    w = dc.width;
    h = dc.height;
    //System.err.println("clipped: " + sx + ", " + sy + " -> " + dx + ", " + dy + " x " + w + ", " + h);

    if (w <= 0 || h <= 0)
      return;

    Rectangle sr = new Rectangle(sx, sy, w, h);
    Rectangle sc = sr.intersection(new Rectangle(0, 0, bufImg.getWidth(), bufImg.getHeight()));
    dx = dx + (sc.x - sx);
    dy = dy + (sc.y - sy);
    sx = sc.x;
    sy = sc.y;
    w = sc.width;
    h = sc.height;

    if (w <= 0 || h <= 0)
      return;

    int transparency = bufImg.getTransparency();
    if (transparency == Transparency.OPAQUE)
      {
        ZPixmap pm = new ZPixmap(gc.display, w, h);
        for (int y = sy; y < sy + h; y++)
          {
            for (int x = sx; x < sx + w; x++)
              {
                int rgb = bufImg.getRGB(x, y);
                pm.set(x - sx, y - sy, rgb);
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
