/* XGraphicsConfiguration.java -- GraphicsConfiguration for X
   Copyright (C) 2006 Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
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

import gnu.x11.Display;
import gnu.x11.Screen;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;

import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;

public class XGraphicsConfiguration
    extends GraphicsConfiguration
{

  static
  {
    System.setProperty("sun.java2d.volatilesurfacemanager",
                       "gnu.java.awt.peer.x.EscherVolatileSurfaceManager");
  }

  XGraphicsDevice device;

  private RenderLoops solidLoops;

  XGraphicsConfiguration(XGraphicsDevice dev)
  {
    device = dev;
  }

  public GraphicsDevice getDevice()
  {
    return device;
  }

  public ColorModel getColorModel()
  {
    // TODO: Implement properly.
    return ColorModel.getRGBdefault();
  }

  public ColorModel getColorModel(int transparency)
  {
    // TODO: Implement properly.
    return ColorModel.getRGBdefault();
  }

  public AffineTransform getDefaultTransform()
  {
    return new AffineTransform();
  }

  public AffineTransform getNormalizingTransform()
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  public Rectangle getBounds()
  {
    Display d = device.getDisplay();
    Screen screen = d.default_screen;
    
    return new Rectangle(0, 0, screen.width, screen.height); 
  }

  /**
   * Determines the size of the primary screen.
   *
   * @return the size of the primary screen
   */
  Dimension getSize()
  {
    // TODO: A GraphicsConfiguration should correspond to a Screen instance.
    Display d = device.getDisplay();
    Screen screen = d.default_screen;
    int w = screen.width;
    int h = screen.height;
    return new Dimension(w, h);
  }

  /**
   * Determines the resolution of the primary screen in pixel-per-inch.
   *
   * @return the resolution of the primary screen in pixel-per-inch
   */
  int getResolution()
  {
    Display d = device.getDisplay();
    Screen screen = d.default_screen;
    int w = screen.width * 254;
    int h = screen.height * 254;
    int wmm = screen.width_in_mm * 10;
    int hmm = screen.height_in_mm * 10;
    int xdpi = w / wmm;
    int ydpi = h / hmm;
    int dpi = (xdpi + ydpi) / 2;
    return dpi;
  }

  synchronized RenderLoops getSolidLoops(SurfaceType stype)
  {
    if (solidLoops == null) {
        solidLoops = SurfaceData.makeRenderLoops(SurfaceType.OpaqueColor,
                                                 CompositeType.SrcNoEa,
                                                 stype);
    }
    return solidLoops;
}
}
