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

  public void draw(SunGraphics2D arg0, Shape arg1)
  {
    throw new UnsupportedOperationException();
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
