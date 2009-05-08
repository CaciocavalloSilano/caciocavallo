/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sun.awt.peer.cacio;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ContainerPeer;
import java.awt.peer.WindowPeer;
import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

class ProxyWindowPeer implements WindowPeer {

    private CacioComponentPeer target;

    ProxyWindowPeer(ProxyWindow pw) {
        target = pw.getTargetPeer();
    }

    public void toFront() {
        // TODO: Maybe call target.toFront() here?
    }

    public void toBack() {
        // TODO: Maybe call target.toBack() here?
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        // TODO: Maybe call target.setAlwaysOnTop here?
    }

    public void updateFocusableWindowState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setModalBlocked(Dialog blocker, boolean blocked) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateMinimumSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateIconImages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Insets getInsets() {
        return new Insets(0, 0, 0, 0);
    }

    public void beginValidate() {
        // Nothing to do here yet.
    }

    public void endValidate() {
        // Nothing to do here yet.
    }

    public void beginLayout() {
        // Nothing to do here yet.
    }

    public void endLayout() {
        // Nothing to do here yet.
    }

    public void restack() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isRestackSupported() {
        return false;
    }

    public boolean isObscured() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canDetermineObscurity() {
        return false;
    }

    public void setVisible(boolean v) {
        // Nothing to do here yet.
    }

    public void setEnabled(boolean e) {
        // Nothing to do here yet.
    }

    public void paint(Graphics g) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void print(Graphics g) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBounds(int x, int y, int width, int height, int op) {
        // Nothing to do here yet.
    }

    public void handleEvent(AWTEvent e) {
        // Nothing to do here.
    }

    public void coalescePaintEvent(PaintEvent e) {
        // Nothing to do here.
    }

    public Point getLocationOnScreen() {
        return target.getLocationOnScreen();
    }

    public Dimension getPreferredSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Dimension getMinimumSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ColorModel getColorModel() {
        return target.getColorModel();
    }

    public Toolkit getToolkit() {
        return target.getToolkit();
    }

    public Graphics getGraphics() {
        return target.getGraphics();
    }

    public FontMetrics getFontMetrics(Font font) {
        return target.getFontMetrics(font);
    }

    public void dispose() {
        // Nothing to do here.
    }

    public void setForeground(Color c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBackground(Color c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFont(Font f) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateCursorImmediately() {
        target.updateCursorImmediately();
    }

    public boolean requestFocus(Component lightweightChild, boolean temporary,
                                boolean focusedWindowChangeAllowed, long time,
                                Cause cause) {

        return target.requestFocus(lightweightChild, temporary,
                                   focusedWindowChangeAllowed, time, cause);

    }

    public boolean isFocusable() {
        return target.isFocusable();
    }

    public Image createImage(ImageProducer producer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Image createImage(int width, int height) {
        return target.createImage(width, height);
    }

    public VolatileImage createVolatileImage(int width, int height) {
        return target.createVolatileImage(width, height);
    }

    public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
        return target.prepareImage(img, w, h, o);
    }

    public int checkImage(Image img, int w, int h, ImageObserver o) {
        return target.checkImage(img, w, h, o);
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        return target.getGraphicsConfiguration();
    }

    public boolean handlesWheelScrolling() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Image getBackBuffer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void destroyBuffers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reparent(ContainerPeer newContainer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isReparentSupported() {
        return false;
    }

    public void layout() {
        // Nothing to do here yet.
    }

    public void applyShape(Region shape) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
