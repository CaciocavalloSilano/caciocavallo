package gnu.java.awt.peer.x;

import java.nio.charset.Charset;

import sun.awt.FontConfiguration;
import sun.font.FontManager;
import sun.java2d.SunGraphicsEnvironment;

public class EscherFontConfiguration
    extends FontConfiguration
{

  public EscherFontConfiguration(FontManager fm)
  {
    super(fm);
  }

  @Override
  protected Charset getDefaultFontCharset(String arg0)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected String getEncoding(String arg0, String arg1)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected String getFaceNameFromComponentFontName(String arg0)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getFallbackFamilyName(String arg0, String arg1)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected String getFileNameFromComponentFontName(String arg0)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void initReorderMap()
  {
    // TODO Auto-generated method stub

  }

}
