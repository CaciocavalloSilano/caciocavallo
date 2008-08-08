/* ZPixmapDataBuffer.java
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

import gnu.x11.Display;
import gnu.x11.image.ZPixmap;

import java.awt.GraphicsEnvironment;
import java.awt.image.DataBuffer;

/**
 * A DataBuffer implementation that is based on a ZPixmap. This is used
 * as backing store for BufferedImages.
 */
class ZPixmapDataBuffer
  extends DataBuffer
{

  /**
   * The backing ZPixmap.
   */
  private ZPixmap zpixmap;

  /**
   * Creates a new ZPixmapDataBuffer with a specified width and height.
   *
   * @param d the X display
   * @param w the width
   * @param h the height
   */
  ZPixmapDataBuffer(int w, int h)
  {
    super(TYPE_BYTE, w * h * 3); // TODO: Support non-24-bit-resolutions.
    GraphicsEnvironment env =
      GraphicsEnvironment.getLocalGraphicsEnvironment();
    XGraphicsDevice dev = (XGraphicsDevice) env.getDefaultScreenDevice();
    Display d = dev.getDisplay();
    zpixmap = new ZPixmap(d, w, h, d.default_pixmap_format);
  }

  /**
   * Creates a ZPixmapDataBuffer from an existing ZPixmap.
   *
   * @param zpixmap the ZPixmap to wrap
   */
  ZPixmapDataBuffer(ZPixmap zpixmap)
  {
    super(TYPE_BYTE, zpixmap.get_data_length());
    this.zpixmap = zpixmap;
  }

  @Override
  public int getElem(int bank, int i)
  {
    return 0xff & zpixmap.get_data_element(i);
  }

  @Override
  public void setElem(int bank, int i, int val)
  {
    zpixmap.set_data_element(i, (byte) val);
  }

  ZPixmap getZPixmap()
  {
    return zpixmap;
  }

}
