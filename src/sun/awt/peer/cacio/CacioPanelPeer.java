package sun.awt.peer.cacio;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.peer.CanvasPeer;
import java.awt.peer.PanelPeer;

import javax.swing.UIManager;

class CacioPanelPeer extends CacioContainerPeer
    implements PanelPeer, CanvasPeer {

    public CacioPanelPeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }

    void init(PlatformWindowFactory pwf) {
        super.init(pwf);
        Color fg = UIManager.getColor("Panel.foreground");
        awtComponent.setForeground(fg);
        Color bg = UIManager.getColor("Panel.background");
        awtComponent.setBackground(bg);
        Font font = UIManager.getFont("Panel.font");
        awtComponent.setFont(font);
    }

    protected void peerPaint(Graphics g, boolean update) {
        // We need to clear the background, because we have no Swing component
        // to do it for us.
        Insets i = getInsets();
        int cx = i.left;
        int cy = i.top;
        int cw = width - i.left - i.right;
        int ch = height - i.top - i.bottom;
        Color bg = UIManager.getColor("Panel.background");
        Color old = g.getColor();
        g.setColor(bg);
        g.clearRect(cx, cy, cw, ch);
        g.setColor(old);
        super.peerPaint(g, update);
    }
}
