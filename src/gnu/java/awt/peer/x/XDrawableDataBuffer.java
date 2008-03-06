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
