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

    @Override
    protected void peerPaint(Graphics g, boolean update) {
        // Canvas never does any painting by itself, not even clearing the
        // background.
    }

    @Override
    public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration gc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
