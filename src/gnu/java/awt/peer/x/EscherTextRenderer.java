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
