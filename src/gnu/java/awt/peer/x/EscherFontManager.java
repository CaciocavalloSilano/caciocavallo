package gnu.java.awt.peer.x;

import sun.awt.FontConfiguration;
import sun.font.FontManager;
import sun.font.FontManagerBase;

public class EscherFontManager
    extends FontManagerBase
{
  // TODO: Just a wrapper class for now
  // probably have to be implemented properly

  @Override
  public String getFontPath(boolean noType1Fonts)
  {
    return "/usr/share/fonts/truetype/freefont/";
  }

  @Override
  protected FontConfiguration createFontConfiguration()
  {
    return new EscherFontConfiguration(this);
  }

  @Override
  public FontConfiguration createFontConfiguration(boolean preferLocaleFonts,
                                                   boolean preferPropFonts)
  {
    return new EscherFontConfiguration(this);
  }
}
