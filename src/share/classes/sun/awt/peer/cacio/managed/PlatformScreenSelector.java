package sun.awt.peer.cacio.managed;

import java.awt.GraphicsConfiguration;

public interface PlatformScreenSelector {
    
    PlatformScreen getPlatformScreen(GraphicsConfiguration config);
}
