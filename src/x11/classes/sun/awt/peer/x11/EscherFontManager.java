package sun.awt.peer.x11;

import sun.awt.*;
import sun.font.*;

public class EscherFontManager extends X11FontManager {
    
    @Override
    protected FontConfiguration createFontConfiguration() {
	FcFontConfiguration fcFontConfig = new FcFontConfiguration(this);
	fcFontConfig.init();
	return fcFontConfig;
    }

    @Override
    protected String getFontPath(boolean b) {
	return "";
    }
}