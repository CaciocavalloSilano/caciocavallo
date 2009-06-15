package sun.awt.peer.cacio;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.peer.CanvasPeer;
import java.awt.peer.PanelPeer;

import javax.swing.JPanel;
import javax.swing.UIManager;

class CacioPanelPeer extends CacioContainerPeer<Panel, JPanel>
    implements PanelPeer {

    public CacioPanelPeer(Panel awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }


    @Override
    JPanel initSwingComponent() {
        return new JPanel();
    }
}
