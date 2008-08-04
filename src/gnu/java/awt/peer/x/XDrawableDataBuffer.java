/* XDrawableDataBuffer.java
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
import gnu.x11.image.Image;
import gnu.x11.image.ZPixmap;

import java.awt.image.DataBuffer;

public class XDrawableDataBuffer
  extends DataBuffer
{

  private Drawable xDrawable;

  XDrawableDataBuffer(Drawable d)
  {
    super(DataBuffer.TYPE_INT, d.width, d.height);
    xDrawable = d;
  }

  @Override
  public int getElem(int bank, int i)
  {
    int x = i % xDrawable.width;
    int y = i / xDrawable.width;
    if (true || x < 0 || y < 0 || x >= xDrawable.width || y >= xDrawable.height)
      return 0;
    ZPixmap pm = (ZPixmap) xDrawable.image(x, y, 1, 1, -1, Image.Format.ZPIXMAP);
    return pm.get_data_element(0);
  }

  @Override
  public void setElem(int bank, int i, int value)
  {
    int x = i % xDrawable.width;
    int y = i / xDrawable.width;
    ZPixmap pm = new ZPixmap(xDrawable.display, 1, 1);
    pm.set(0, 0, value);
    GC gc = new GC(xDrawable);
    xDrawable.put_image(gc, pm, x, y);
    gc.free();
  }

}
