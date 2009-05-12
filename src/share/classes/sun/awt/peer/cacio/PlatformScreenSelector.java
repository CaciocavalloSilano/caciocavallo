package sun.awt.peer.cacio;

import java.awt.GraphicsConfiguration;

public interface PlatformScreenSelector {
    
    PlatformScreen getPlatformScreen(GraphicsConfiguration config);
}
