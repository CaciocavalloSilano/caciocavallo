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
  protected Charset getDefaultFontCharset(String fontName)
  {
    // TODO Auto-generated method stub
    System.err.println("IMPLEMENT ME: EscherFontConfiguration.getDefaultFontCharset");
    return null;
  }

  @Override
  protected String getEncoding(String awtFontName,
                               String characterSubsetName)
  {
    // TODO Auto-generated method stub
    System.err.println("IMPLEMENT ME: EscherFontConfiguration.getEncoding");
    return null;
  }

  @Override
  protected String getFaceNameFromComponentFontName(String componentFontName)
  {
    // TODO Auto-generated method stub
    //System.err.println("IMPLEMENT ME: EscherFontConfiguration.getFaceNameFromComponentFontName: " + componentFontName);
    // MFontConfiguration also returns null here, so maybe this is ok.
    return null;
  }

  @Override
  public String getFallbackFamilyName(String fontName, String defaultFallback)
  {
    // TODO Auto-generated method stub
    System.err.println("IMPLEMENT ME: EscherFontConfiguration.getFallbackFamilyName");
    return null;
  }

  @Override
  protected String getFileNameFromComponentFontName(String componentFontName)
  {
    // TODO Auto-generated method stub
    System.err.println("IMPLEMENT ME: EscherFontConfiguration.getFileNameFromComponentFontName: " + componentFontName);
    return null;
  }

  @Override
  protected void initReorderMap()
  {
    // TODO Auto-generated method stub
    System.err.println("IMPLEMENT ME: EscherFontConfiguration.initReorderMap");
  }

}
