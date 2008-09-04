package sun.awt.peer.cacio;

import java.awt.Component;
import java.awt.peer.PanelPeer;

class CacioPanelPeer extends CacioContainerPeer implements PanelPeer {

    public CacioPanelPeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }

}
