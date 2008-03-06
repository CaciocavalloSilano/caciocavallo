package gnu.java.awt.peer.x;

import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class XDrawableRaster
    extends WritableRaster
{

  XDrawableRaster(SampleModel sm, DataBuffer db, Point p)
  {
    super(sm, db, p);
  }
}
