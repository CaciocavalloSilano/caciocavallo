package gnu.java.awt.peer.x;

import sun.awt.FontConfiguration;
import sun.font.FontManager;

public class EscherFontManager
    extends FontManager
{

  @Override
  public String getFontPath(boolean noType1Fonts)
  {
    return "/var/lib/defoma/x-ttcidfont-conf.d/dirs/TrueType";
  }

  @Override
  protected FontConfiguration createFontConfiguration()
  {
    return new EscherFontConfiguration(this);
  }

  @Override
  public FontConfiguration createFontConfiguration(boolean preferLocaleFonts, boolean preferPropFonts)
  {
    return new EscherFontConfiguration(this);
  }

}
