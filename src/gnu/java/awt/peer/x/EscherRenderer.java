/* EscherRenderer.java
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

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.ShapeSpanIterator;

public class EscherRenderer
  implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe
{

  private static EscherRenderer instance;

  static EscherRenderer getInstance()
  {
    if (instance == null)
      {
        instance = new EscherRenderer();
      }
    return instance;
  }

  /**
   * Singleton constructor.
   */
  private EscherRenderer()
  {
    // Nothing to do here.
  }

  public void drawArc(SunGraphics2D arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6)
  {
    throw new UnsupportedOperationException();
  }

  public void drawLine(SunGraphics2D sg2d, int x1, int y1, int x2, int y2)
  {
    SunToolkit.awtLock();
    try
      {
        XDrawableSurfaceData xdsf = (XDrawableSurfaceData) sg2d.surfaceData;
        GC gc = xdsf.getRenderGC(sg2d);
        Drawable xDrawable = xdsf.getDrawable();
        int transX = sg2d.transX;
        int transY = sg2d.transY;
        xDrawable.line(gc, x1 + transX, y1 + transY, x2 + transX, y2 + transY);
      }
    finally
      {
        SunToolkit.awtUnlock();
      }
  }

  public void drawOval(SunGraphics2D arg0, int arg1, int arg2, int arg3, int arg4)
  {
    throw new UnsupportedOperationException();
  }

  public void drawPolygon(SunGraphics2D arg0, int[] arg1, int[] arg2, int arg3)
  {
    throw new UnsupportedOperationException();
  }

  public void drawPolyline(SunGraphics2D arg0, int[] arg1, int[] arg2, int arg3)
  {
    throw new UnsupportedOperationException();
  }

  public void drawRect(SunGraphics2D sg2d, int x, int y, int w, int h)
  {
    SunToolkit.awtLock();
    try
      {
        XDrawableSurfaceData xdsf = (XDrawableSurfaceData) sg2d.surfaceData;
        GC gc = xdsf.getRenderGC(sg2d);
        Drawable xDrawable = xdsf.getDrawable();
        xDrawable.rectangle(gc, x + sg2d.transX, y + sg2d.transY, w, h);
      }
    finally
      {
        SunToolkit.awtUnlock();
      }
  }

  public void drawRoundRect(SunGraphics2D arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6)
  {
    throw new UnsupportedOperationException();
  }

  public void fillArc(SunGraphics2D arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6)
  {
    throw new UnsupportedOperationException();
  }

  public void fillOval(SunGraphics2D arg0, int arg1, int arg2, int arg3, int arg4)
  {
    throw new UnsupportedOperationException();
  }

  public void fillPolygon(SunGraphics2D arg0, int[] arg1, int[] arg2, int arg3)
  {
    throw new UnsupportedOperationException();
  }

  public void fillRect(SunGraphics2D sg2d, int x, int y, int w, int h)
  {
    SunToolkit.awtLock();
    try
      {
        xFillRect(sg2d, x + sg2d.transX, y + sg2d.transY, w, h);
      }
    finally
      {
        SunToolkit.awtUnlock();
      }
  }

  public void fillRoundRect(SunGraphics2D arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6)
  {
    throw new UnsupportedOperationException();
  }

  public void draw(SunGraphics2D sg2d, Shape s)
  {
    if (sg2d.strokeState == sg2d.STROKE_THIN)
      {
        // Delegate to drawPolygon() if possible...
        if (s instanceof Polygon &&
            sg2d.transformState < sg2d.TRANSFORM_TRANSLATESCALE)
          {
            Polygon p = (Polygon) s;
            drawPolygon(sg2d, p.xpoints, p.ypoints, p.npoints);
            return;
          }

        // Otherwise we will use drawPath() for
        // high-quality thin paths.
        doPath(sg2d, s, false);
      }
    else if (sg2d.strokeState < sg2d.STROKE_CUSTOM)
      {
        // REMIND: X11 can handle uniform scaled wide lines
        // and dashed lines itself if we set the appropriate
        // XGC attributes (TBD).
        ShapeSpanIterator si = LoopPipe.getStrokeSpans(sg2d, s);
        try
          {
            SunToolkit.awtLock();
            try
              {
                fillSpans(sg2d, si, 0, 0);
          } finally {
            SunToolkit.awtUnlock();
          }
        } finally {
          si.dispose();
        }
  } else {
      fill(sg2d, sg2d.stroke.createStrokedShape(s));
  }
  }

  public void fill(SunGraphics2D sg2d, Shape s)
  {
    if (sg2d.strokeState == SunGraphics2D.STROKE_THIN)
      {
        // Delegate to fillPolygon() if possible...
        if (s instanceof Polygon &&
            sg2d.transformState < SunGraphics2D.TRANSFORM_TRANSLATESCALE)
          {
            Polygon p = (Polygon) s;
            fillPolygon(sg2d, p.xpoints, p.ypoints, p.npoints);
            return;
          }
        else
          {
            // Otherwise we will use fillPath() for high-quality fills.
            doPath(sg2d, s, true);
            return;
          }
      }
    else
      {
        AffineTransform at;
        int transx, transy;
        if (sg2d.transformState < SunGraphics2D.TRANSFORM_TRANSLATESCALE)
          {
            // Transform (translation) will be done by XFillSpans
            at = null;
            transx = sg2d.transX;
            transy = sg2d.transY;
          }
        else
          {
            // Transform will be done by the PathIterator
            at = sg2d.transform;
            transx = transy = 0;
          }

        ShapeSpanIterator ssi = LoopPipe.getFillSSI(sg2d);
        try
          {
            // Subtract transx/y from the SSI clip to match the
            // (potentially untranslated) geometry fed to it
            Region clip = sg2d.getCompClip();
            ssi.setOutputAreaXYXY(clip.getLoX() - transx,
                                  clip.getLoY() - transy,
                                  clip.getHiX() - transx,
                                  clip.getHiY() - transy);
            ssi.appendPath(s.getPathIterator(at));
            SunToolkit.awtLock();
            try
              {
                fillSpans(sg2d, ssi, transx, transy);
              }
            finally
              {
                SunToolkit.awtUnlock();
              }
          }
        finally
          {
            ssi.dispose();
          }
      }
  }

  private void fillSpans(SunGraphics2D sg2d, ShapeSpanIterator ssi,
                         int transX, int transY)
  {
    System.err.println("IMPLEMENT: EscherRenderer.fillSpans()");
  }

  private void doPath(SunGraphics2D sg2d, Shape s, boolean isFill)
  {
    SunToolkit.awtLock();
    try
      {
        ShapeSpanIterator ssi = new ShapeSpanIterator(false);
        try
          {
            Path2D.Float p2df = new Path2D.Float(s, sg2d.transform);
            ssi.setOutputArea(sg2d.clipRegion);
            ssi.appendPath(p2df.getPathIterator(null));
            int spanbox[] = new int[4];
            while (ssi.nextSpan(spanbox))
              {
                int x = spanbox[0];
                int y = spanbox[1];
                int w = spanbox[2] - x;
                int h = spanbox[3] - y;
                xFillRect(sg2d, x, y, w, h);
              }  

          }
        finally
          {
            ssi.dispose();
          }
      }
    finally
      {
        SunToolkit.awtUnlock();
      }
  }

  private void doPath(SunGraphics2D sg2d, int transX, int transY,
                      Path2D.Float p2df, boolean isFill)
  {
  }

  private void xFillRect(SunGraphics2D sg2d, int x, int y, int w, int h)
  {
    XDrawableSurfaceData xdsf = (XDrawableSurfaceData) sg2d.surfaceData;
    GC gc = xdsf.getRenderGC(sg2d);
    Drawable xDrawable = xdsf.getDrawable();
    xDrawable.fill_rectangle(gc, x, y, w, h);
    //System.err.println("fillRect: " + x + ", " + y + ", " + w + ", " + h);
  }
}
