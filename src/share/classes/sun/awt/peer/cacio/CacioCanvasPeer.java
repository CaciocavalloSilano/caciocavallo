/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sun.awt.peer.cacio;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.peer.CanvasPeer;
import javax.swing.JPanel;
import javax.swing.UIManager;

class CacioCanvasPeer extends CacioComponentPeer<Canvas, JPanel>
                      implements CanvasPeer {

    public CacioCanvasPeer(Canvas awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }

    // TODO: Consolidate CacioCanvasPeer and CacioPanelPeer. Avoid duplication.
    @Override
    void init(PlatformWindowFactory pwf) {
        super.init(pwf);
        Canvas awtComponent = getAWTComponent();
        Color fg = UIManager.getColor("Panel.foreground");
        awtComponent.setForeground(fg);
        Color bg = UIManager.getColor("Panel.background");
        awtComponent.setBackground(bg);
        Font font = UIManager.getFont("Panel.font");
        awtComponent.setFont(font);
    }

    @Override
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

    @Override
    public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration gc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
