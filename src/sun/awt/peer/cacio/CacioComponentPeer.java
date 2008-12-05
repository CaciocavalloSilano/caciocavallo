/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.awt.peer.cacio;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;

import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;

import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;

import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.CausedFocusEvent.Cause;

import sun.awt.event.ComponentReshapeEvent;

import sun.font.FontDesignMetrics;

import sun.java2d.pipe.Region;

/**
 * The central superclass for all peers. In the Cacio peers, all components are
 * acting like this:
 * <ul>
 * <li>Each component has its own native window. The actual implementation of
 * this window has to be done in an implementation of {@link PlatformWindow}.
 * </li>
 * <li>The native window implementation feeds all relevant AWT events back into
 * the CacioComponentPeer for further processing.</li>
 * <li>Each component is drawn using a corresponding Swing component (e.g. an
 * AWT Button is manages its Swing JButton in the background).</li>
 * <li>The logic of the components is also implemented by the corresponding
 * Swing component.</li>
 * </ul>
 */
class CacioComponentPeer implements ComponentPeer, CacioComponent {

    /**
     * The AWT component that corresponds to this component peer.
     */
    Component awtComponent;

    /**
     * The backing Swing component. Some components don't have a backing
     * swing component and leave that to <code>null</code>.
     */
    private CacioSwingComponent swingComponent;

    /**
     * The underlying native platform window.
     */
    PlatformWindow platformWindow;

    /**
     * The current repaint area.
     */
    private Rectangle paintArea;

    /**
     * Creates a new CacioComponentPeer.
     * 
     * @param awtC
     *            the AWT component
     * @param pwf
     *            a platform window factory for creating the platform window
     */
    CacioComponentPeer(Component awtC, PlatformWindowFactory pwf) {
        awtComponent = awtC;
        init(pwf);
    }

    /**
     * Initializes this peer. Most importantly, this should initialize the
     * underlying platform window using the specified
     * {@link PlatformWindowFactory}.
     * 
     * @param pwf
     */
    void init(PlatformWindowFactory pwf) {
        
        // Figure out the heavyweight parent window.
        PlatformWindow parent = null; // Assume toplevel window for the start.
        Component parentComp = awtComponent.getParent();
        while (parentComp != null && parent == null) {
            if (parentComp.isLightweight()) {
                parentComp = parentComp.getParent();
            } else {
                CacioComponentPeer parentPeer =
                    (CacioComponentPeer) parentComp.getPeer();
                parent = parentPeer.platformWindow;
            }
        }
        platformWindow = pwf.createPlatformWindow(this, parent);

        initSwingComponent();
    }

    void initSwingComponent() {
        // By default, do nothing.
    }

    /**
     * Disposes the peer object and releases all associated native resources.
     */
    @Override
    public void dispose() {

        platformWindow.dispose();
	CacioToolkit.disposePeer(awtComponent, this);
    }

    @Override
    public ColorModel getColorModel() {

        return platformWindow.getColorModel();
    }

    @Override
    public Graphics getGraphics() {

        return platformWindow.getGraphics();
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {

        return platformWindow.getGraphicsConfiguration();
    }

    @Override
    public FontMetrics getFontMetrics(Font font) {

        return FontDesignMetrics.getMetrics(font);
    }

    @Override
    public Point getLocationOnScreen() {

        return platformWindow.getLocationOnScreen();
    }

    @Override
    public Dimension getMinimumSize() {

        Dimension min;
        if (swingComponent != null) {
            min = swingComponent.getJComponent().getMinimumSize();
        } else {
            min = new Dimension(0, 0);
        }
        return min;
    }

    @Override
    public Dimension getPreferredSize() {

        Dimension pref;
        if (swingComponent != null) {
            pref = swingComponent.getJComponent().getPreferredSize();
        } else {
            pref = new Dimension(0, 0);
        }
        return pref;
    }

    @Override
    public Toolkit getToolkit() {

        return Toolkit.getDefaultToolkit();
    }

    @Override
    public void handleEvent(AWTEvent e) {

        switch (e.getID())
        {
        case PaintEvent.UPDATE:
        case PaintEvent.PAINT:
          if (awtComponent.isShowing())
            {
              Rectangle clip ;
              synchronized (this)
                {
                  coalescePaintEvent((PaintEvent) e);
                  assert paintArea != null;
                  clip = paintArea;
                  paintArea = null;
                }
              Graphics g = awtComponent.getGraphics();
              try
                {
                  g.clipRect(clip.x, clip.y, clip.width, clip.height);
                  peerPaint(g, e.getID() == PaintEvent.UPDATE);
                }
              finally
                {
                  g.dispose();
                }
            }
          break;
        case MouseEvent.MOUSE_PRESSED:
        case MouseEvent.MOUSE_RELEASED:
        case MouseEvent.MOUSE_CLICKED:
        case MouseEvent.MOUSE_ENTERED:
        case MouseEvent.MOUSE_EXITED:
          handleMouseEvent((MouseEvent) e);
          break;
        case MouseEvent.MOUSE_MOVED:
        case MouseEvent.MOUSE_DRAGGED:
          handleMouseMotionEvent((MouseEvent) e);
          break;
        case KeyEvent.KEY_PRESSED:
        case KeyEvent.KEY_RELEASED:
        case KeyEvent.KEY_TYPED:
          handleKeyEvent((KeyEvent) e);
          break;
        case FocusEvent.FOCUS_GAINED:
        case FocusEvent.FOCUS_LOST:
          handleFocusEvent((FocusEvent)e);
          break;
        default:
          // Other event types are not handled here.
          break;
        }

    }

    /**
     * Handles mouse events on the component. This is usually forwarded to the
     * SwingComponent's processMouseEvent() method.
     *
     * @param e the mouse event
     */
    private void handleMouseEvent(MouseEvent e) {
        
        if (swingComponent != null)
            swingComponent.handleMouseEvent(e);
    }

    /**
     * Handles mouse motion events on the component. This is usually forwarded
     * to the SwingComponent's processMouseMotionEvent() method.
     *
     * @param e the mouse motion event
     */
    private void handleMouseMotionEvent(MouseEvent e) {
        
        if (swingComponent != null)
            swingComponent.handleMouseMotionEvent(e);
    }

    /**
     * Handles key events on the component. This is usually forwarded to the
     * SwingComponent's processKeyEvent() method.
     *
     * @param e the key event
     */
    private void handleKeyEvent(KeyEvent e) {
        
        if (swingComponent != null)
            swingComponent.handleKeyEvent(e);
    }

    /**
     * Handles focus events on the component. This is usually forwarded to the
     * SwingComponent's processFocusEvent() method.
     *
     * @param e the key event
     */
    private void handleFocusEvent(FocusEvent e) {
        
        if (swingComponent != null)
            swingComponent.handleFocusEvent(e);
    }

    private void peerPaint(Graphics g, boolean update) {
        
        if (swingComponent != null) {
            if (update) {
                swingComponent.getJComponent().update(g);
            } else {
                swingComponent.getJComponent().paint(g);
            }
        }
        
        Graphics userGraphics = g.create();
        try {
            if (update) {
                awtComponent.update(userGraphics);
            } else {
                awtComponent.paint(userGraphics);
            }
        } finally {
            userGraphics.dispose();
        }
    }

    @Override
    public boolean handlesWheelScrolling() {

        // TODO: Implement this correctly
        System.out.println("IMPLEMENT ME: CacioComponentPeer.handlesWheelScrolling");
        return false;

    }

    @Override
    public boolean isFocusable() {

        boolean ret;
        if (swingComponent != null) {
            ret = swingComponent.getJComponent().isFocusable();
        } else {
            ret = false;
        }
        return ret;
    }

    @Override
    public boolean isReparentSupported() {

        return platformWindow.isReparentSuppored();
    }

    @Override
    public void reparent(ContainerPeer newContainer) {

        platformWindow.reparent(newContainer);
    }

    @Override
    public void layout() {

        // TODO: Implement this correctly.
        System.out.println("IMPLEMENT ME: CacioComponentPeer.layout");
    }

    @Override
    public void paint(Graphics g) {

        // TODO: Implement this correctly.
        System.out.println("IMPLEMENT ME: CacioComponentPeer.paint");
    }

    @Override
    public void print(Graphics g) {

        // TODO: Implement this correctly.
        System.out.println("IMPLEMENT ME: CacioComponentPeer.print");
    }

    public boolean requestFocus(Component lightweightChild, boolean temporary,
            boolean focusedWindowChangeAllowed, long time, Cause cause) {

        return platformWindow.requestFocus(lightweightChild, temporary,
                                           focusedWindowChangeAllowed,
                                           time, cause);
    }

    public void setBackground(Color c) {

        // TODO: Implement this correctly.
        System.out.println("IMPLEMENT ME: CacioComponentPeer.setBackground");
    }

    public void setFont(Font f) {

        // TODO: Implement this correctly.
        System.out.println("IMPLEMENT ME: CacioComponentPeer.setFont");
    }

    public void setForeground(Color c) {

        // TODO: Implement this correctly.
        System.out.println("IMPLEMENT ME: CacioComponentPeer.setForeground");
    }

    public void setBounds(int x, int y, int width, int height, int op) {

        platformWindow.setBounds(x, y, width, height, op);

        if (swingComponent != null) {
            swingComponent.getJComponent().setBounds(x, y, width, height);
        }
    }

    public void setEnabled(boolean b) {

        if (swingComponent != null) {
            swingComponent.getJComponent().setEnabled(b);
        }

    }

    public void setVisible(boolean b) {

        platformWindow.setVisible(b);

    }

    public void updateCursorImmediately() {

        platformWindow.updateCursorImmediately();

    }

    public int checkImage(Image img, int w, int h, ImageObserver o) {

        return Toolkit.getDefaultToolkit().checkImage(img, w, h, o);

    }

    public boolean prepareImage(Image img, int w, int h, ImageObserver o) {

        return Toolkit.getDefaultToolkit().prepareImage(img, w, h, o);

    }

    public Image createImage(ImageProducer producer) {

        return Toolkit.getDefaultToolkit().createImage(producer);

    }

    public Image createImage(int width, int height) {

        GraphicsConfiguration gc = getGraphicsConfiguration();
        return gc.createCompatibleImage(width, height);

    }

    public VolatileImage createVolatileImage(int width, int height) {

        GraphicsConfiguration gc = getGraphicsConfiguration();
        return gc.createCompatibleVolatileImage(width, height);

    }

    public void createBuffers(int numBuffers, BufferCapabilities caps)
        throws AWTException {

        // TODO: Implement this correctly.
        System.out.println("IMPLEMENT ME: CacioComponentPeer.createBuffers");

    }

    public void destroyBuffers() {

        // TODO: Implement this correctly.
        System.out.println("IMPLEMENT ME: CacioComponentPeer.destroyBuffers");

    }

    public void flip(int x1, int y1, int x2, int y2,
                     BufferCapabilities.FlipContents flipAction) {

        // TODO: Implement this correctly.
        System.out.println("IMPLEMENT ME: CacioComponentPeer.flip");

    }
    
    public Image getBackBuffer() {

        // TODO: Implement this correctly.
        System.out.println("IMPLEMENT ME: CacioComponentPeer.getBackBuffer");
        return null;
    }

    public void coalescePaintEvent(PaintEvent e) {

        synchronized (this) {
            Rectangle newRect = e.getUpdateRect();
            if (paintArea == null)
                paintArea = newRect;
            else
                Rectangle.union(paintArea, newRect, paintArea);
        }

    }

    public void applyShape(Region shape) {

        platformWindow.applyShape(shape);

    }

    public boolean canDetermineObscurity() {

        return platformWindow.canDetermineObscurity();

    }

    public boolean isObscured() {

        return platformWindow.isObscured();

    }

    CacioSwingComponent getSwingComponent() {
        return swingComponent;
    }

    /**
     * Sets the swing component of this peer.
     *
     * @param swingComp the swing component to set
     */
    void setSwingComponent(CacioSwingComponent swingComp) {
        swingComponent = swingComp;
    }

    public PlatformWindow getPlatformWindow() {
        return platformWindow;
    }

    public Component getAWTComponent() {
        return awtComponent;
    }

    public void handlePeerEvent(AWTEvent event) {
        // ComponentReshapeEvents are special events to notify AWT components
        // about size changes without triggering any layout activity. They
        // must not be sent over the event queue and not made visible
        // to the application in any way.
        if (event instanceof ComponentReshapeEvent) {
            awtComponent.dispatchEvent(event);
        } else {
            SunToolkit.postEvent(AppContext.getAppContext(), event);
        }
    }
}
