/* EscherTextRenderer.java
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
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.GlyphListPipe;

class EscherTextRenderer
  extends GlyphListPipe
{

  @Override
  protected void drawGlyphList(SunGraphics2D sg2d, GlyphList gl)
  {
    XDrawableSurfaceData xdsd = (XDrawableSurfaceData) sg2d.surfaceData;
    Drawable xd = xdsd.getDrawable();
    GC gc = xdsd.getRenderGC(sg2d);
    int strbounds[] = gl.getBounds();
    int numglyphs = gl.getNumGlyphs();
    for (int gi = 0; gi < numglyphs; gi++)
      {
        gl.setGlyphIndex(gi);
        int metrics[] = gl.getMetrics();
        byte bits[] = gl.getGrayBits();
        int glyphx = metrics[0];
        int glyphy = metrics[1];
        int glyphw = metrics[2];
        int glyphh = metrics[3];
        int off = 0;
        for (int j = 0; j < glyphh; j++)
          {
            for (int i = 0; i < glyphw; i++)
              {
                int dx = glyphx + i;
                int dy = glyphy + j;
                int alpha = bits[off++];
                drawPixel(xd, gc, alpha, dx, dy);
              }
          }
      }
  }

  private void drawPixel(Drawable xd, GC gc, int alpha, int dx, int dy)
  {
    if (alpha == 0xffffffff)
      xd.point(gc, dx, dy);
  }

}
