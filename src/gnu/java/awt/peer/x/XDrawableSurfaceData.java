/* XDrawableSurfaceData.java
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
import gnu.x11.color.ColorMapper;

import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

import sun.awt.SunHints;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.PixelToShapeConverter;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.ValidatePipe;

class XDrawableSurfaceData
    extends SurfaceData
{

  static final String DESC_ESCHER = "Escher drawable";
  static final String DESC_ESCHER_INT = "Escher int drawable";
  static final String DESC_ESCHER_INT_RGB = "Escher int RGB drawable";

  static final SurfaceType Escher =
    SurfaceType.Any.deriveSubType(DESC_ESCHER_INT_RGB);
  static final SurfaceType EscherInt =
    Escher.deriveSubType(DESC_ESCHER_INT);
  static final SurfaceType EscherIntRgb =
    EscherInt.deriveSubType(DESC_ESCHER_INT_RGB);

  // NOTE: Do not move this up, we need the constants above in the
  // initialization of the blit loops.
  static
  {
    EscherBlitLoops.register();
  }

    private static class LazyPipe extends ValidatePipe
  {
    public boolean validate(SunGraphics2D sg2d)
    {
      XDrawableSurfaceData xdsd = (XDrawableSurfaceData) sg2d.surfaceData;
        if (! xdsd.isDrawableValid())
          {
            return false;
          }
        xdsd.makePipes();
        return super.validate(sg2d);
    }
}

  private EscherRenderer primitivePipe;
  private PixelToShapeConverter complexPipe;
  private EscherTextRenderer textPipe;
  private LazyPipe lazyPipe;

  private RenderLoops solidLoops;

  private GC xgc;
  private Drawable xDrawable;

  /**
   * The graphics configuration for that surface.
   */
  private XGraphicsConfiguration graphicsConfig;

  XDrawableSurfaceData(XGraphicsConfiguration gc, Drawable xd,
                       SurfaceType type, ColorModel cm)
  {
    super(type, cm);
    xDrawable = xd;
    lazyPipe = new LazyPipe();
    solidLoops = gc.getSolidLoops(type);
    graphicsConfig = gc;
  }

  @Override
  public Rectangle getBounds()
  {
    return new Rectangle(0, 0, xDrawable.width, xDrawable.height);
  }

  @Override
  public Object getDestination()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public GraphicsConfiguration getDeviceConfiguration()
  {
    return graphicsConfig;
  }

  private WritableRaster raster;

  @Override
  public Raster getRaster(int x, int y, int w, int h)
  {
    if (raster == null)
      {
        int width = xDrawable.width;
        int height = xDrawable.height;
        DataBuffer b = new XDrawableDataBuffer(xDrawable);
        ColorModel cm = ColorModel.getRGBdefault();
        SampleModel sm = cm.createCompatibleSampleModel(width, height);
        WritableRaster r =  new XDrawableRaster(sm, b, new Point(0, 0));
        raster = r;
      }
    return raster;
  }

  @Override
  public SurfaceData getReplacement()
  {
    throw new UnsupportedOperationException();
  }

  public void validatePipe(SunGraphics2D sg2d) {
    if (sg2d.antialiasHint != SunHints.INTVAL_ANTIALIAS_ON
        && sg2d.paintState <= SunGraphics2D.PAINT_ALPHACOLOR
        && (sg2d.compositeState <= SunGraphics2D.COMP_ISCOPY
            || sg2d.compositeState == SunGraphics2D.COMP_XOR))
    {
      if (complexPipe == null)
        {
          sg2d.drawpipe = lazyPipe;
          sg2d.fillpipe = lazyPipe;
          sg2d.shapepipe = lazyPipe;
          sg2d.imagepipe = lazyPipe;
          sg2d.textpipe = lazyPipe;
          return;
        }

      if (sg2d.clipState == SunGraphics2D.CLIP_SHAPE)
        {
          // Do this to init textpipe correctly; we will override the
          // other non-text pipes below
          // REMIND: we should clean this up eventually instead of
          // having this work duplicated.
          super.validatePipe(sg2d);
        }
      else
        {
          switch (sg2d.textAntialiasHint)
            {
            case SunHints.INTVAL_TEXT_ANTIALIAS_DEFAULT:
              // equating to OFF which it is for us.
            case SunHints.INTVAL_TEXT_ANTIALIAS_OFF:
              // Use X11 pipe even if DGA is available since DGA
              // text slows everything down when mixed with X11 calls
              if (sg2d.compositeState == SunGraphics2D.COMP_ISCOPY)
                {
                  sg2d.textpipe = textPipe;
                }
              else
                {
                  sg2d.textpipe = solidTextRenderer;
                }
              break;
            case SunHints.INTVAL_TEXT_ANTIALIAS_ON:
              // Remind: may use Xrender for these when composite is
              // copy as above, or if remote X11.
              sg2d.textpipe = aaTextRenderer;
              break;
            default:
              switch (sg2d.getFontInfo().aaHint)
                {
                case SunHints.INTVAL_TEXT_ANTIALIAS_LCD_HRGB:
                case SunHints.INTVAL_TEXT_ANTIALIAS_LCD_VRGB:
                  sg2d.textpipe = lcdTextRenderer;
                  break;
                case SunHints.INTVAL_TEXT_ANTIALIAS_OFF:
                  // Use X11 pipe even if DGA is available since DGA
                  // text slows everything down when mixed with X11 calls
                  if (sg2d.compositeState == SunGraphics2D.COMP_ISCOPY)
                    {
                      sg2d.textpipe = textPipe;
                    }
                  else
                    {
                      sg2d.textpipe = solidTextRenderer;
                    }
                  break;
                case SunHints.INTVAL_TEXT_ANTIALIAS_ON:
                  sg2d.textpipe = aaTextRenderer;
                  break;
                default:
                  sg2d.textpipe = solidTextRenderer;
                }
            }
        }

        if (sg2d.transformState >= SunGraphics2D.TRANSFORM_TRANSLATESCALE)
          {
            sg2d.drawpipe = complexPipe;
            sg2d.fillpipe = complexPipe;
          }
        else if (sg2d.strokeState != SunGraphics2D.STROKE_THIN)
          {
            sg2d.drawpipe = complexPipe;
            sg2d.fillpipe = primitivePipe;
          }
        else
          {
            sg2d.drawpipe = primitivePipe;
            sg2d.fillpipe = primitivePipe;
          }
        sg2d.shapepipe = primitivePipe;
        sg2d.imagepipe = imagepipe;

        // This is needed for AA text.
        // Note that even an X11TextRenderer can dispatch AA text
        // if a GlyphVector overrides the AA setting.
        // We use getRenderLoops() rather than setting solidloops
        // directly so that we get the appropriate loops in XOR mode.
        sg2d.loops = getRenderLoops(sg2d);
      }
    else
      {
        super.validatePipe(sg2d);
      }
  }

  public RenderLoops getRenderLoops(SunGraphics2D sg2d)
  {
    if (sg2d.paintState <= SunGraphics2D.PAINT_ALPHACOLOR &&
        sg2d.compositeState <= SunGraphics2D.COMP_ISCOPY)
      {
        return solidLoops;
      }
    return super.getRenderLoops(sg2d);
  }

  private boolean isDrawableValid()
  {
    return true;
  }

  public synchronized void makePipes()
  {
    if (primitivePipe == null)
      {
        SunToolkit.awtLock();
        try
          {
            xgc = new GC(xDrawable);
          }
        finally
          {
            SunToolkit.awtUnlock();
          }
        primitivePipe = EscherRenderer.getInstance();
        complexPipe = new PixelToShapeConverter(primitivePipe);
        textPipe = new EscherTextRenderer();
    }
  }

  public void invalidate()
  {
    super.invalidate();
    xgc.free();
  }

  /**
   * Configures and returns the current GC.
   *
   * @return the GC for this surface
   */
  GC getRenderGC(SunGraphics2D sg2d)
  {
    int pixel = ColorMapper.getInstace().
            convertToNativePixel(sg2d.pixel,
                                 xDrawable.display.getDefaultVisual());
      
    xgc.set_foreground(pixel);
    Region c = sg2d.clipRegion;
    if (c.isRectangular())
      {
        gnu.x11.Rectangle clip = new gnu.x11.Rectangle(c.getLoX(), c.getLoY(),
                                                       c.getWidth(),
                                                       c.getHeight());
        xgc.set_clip_rectangles(0, 0,
                                new gnu.x11.Rectangle[]{clip}, GC.UN_SORTED);
      }
    else
      {
        throw new UnsupportedOperationException();
      }
    return xgc;
  }

  GC getBlitGC(Region c)
  {
      if (c != null) {
          if (c.isRectangular())
          {
              gnu.x11.Rectangle clip = new gnu.x11.Rectangle(c.getLoX(), c.getLoY(),
                      c.getWidth(),
                      c.getHeight());
              xgc.set_clip_rectangles(0, 0,
                      new gnu.x11.Rectangle[]{clip}, GC.UN_SORTED);
          }
          else
          {
              throw new UnsupportedOperationException();
          }
      }
    return xgc;
  }

  Drawable getDrawable()
  {
    return xDrawable;
  }
}
