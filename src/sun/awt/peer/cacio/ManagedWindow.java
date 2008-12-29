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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.event.PaintEvent;
import java.awt.peer.ContainerPeer;
import java.awt.image.ColorModel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

/**
 * A {@link PlatformWindow} implementation that uses a ManagedWindowContainer
 * as parent and implements all the window management logic in Java. This
 * is most useful on target systems without or with limited window support.
 */
public class ManagedWindow
    extends AbstractManagedWindowContainer
    implements PlatformWindow {

    /**
     * The parent container.
     */
    private ManagedWindowContainer parent;

    /**
     * The corresponding cacio component.
     */
    private CacioComponent cacioComponent;

    /**
     * The bounds of this window, relative to the parent container.
     */
    private int x, y, width, height;

    /**
     * The background color of the component window.
     */
    private Color background;

    /**
     * The foreground color of the component window.
     */
    private Color foreground;

    /**
     * The font of the component window.
     */
    private Font font;

    /**
     * Indicates if this window is visible or not.
     */
    private boolean visible;

    /**
     * Constructs a new ManagedWindow, that has the specified parent
     * container.
     *
     * @param p the parent container
     * @param cacioComp the cacio component for this window
     */
    ManagedWindow(ManagedWindowContainer p, CacioComponent cacioComp) {
        super();
        parent = p;
        cacioComponent = cacioComp;
        parent.add(this);
        Component c = cacioComponent.getAWTComponent();
        setBounds(c.getX(), c.getY(), c.getWidth(), c.getHeight(), 0);
    }

    @Override
    public ColorModel getColorModel() {
        return parent.getColorModel();
    }

    @Override
    public Graphics2D getClippedGraphics(List<Rectangle> clipRects) {
        // Translate all clip rectangles to parent's coordinate system.
        if (clipRects != null) {
            for (Rectangle r : clipRects) {
                r.x += x;
                r.y += y;
            }
        }
        return prepareClippedGraphics(clipRects);
    }

    @Override
    public Graphics2D getGraphics() {
        // Check if we have obscuring siblings and add their clip
        // rectangles to the list.
        Graphics2D g2d = prepareClippedGraphics(null);
        g2d.setColor(foreground);
        g2d.setFont(font);
        g2d.setBackground(background);
        return g2d;
    }

    private Graphics2D prepareClippedGraphics(List<Rectangle> clipRects) {
        clipRects = addClipRects(null);
        // Ask parent for clipped graphics.
        Graphics2D pg = parent.getClippedGraphics(clipRects);
        // Translate and clip to our own coordinate system.
        return (Graphics2D) pg.create(x, y, width, height);
    }

    /**
     * This method adds any necessary clip rectangles to the specified
     * list. If the list is null and rectangles need to be added,
     * then a new one is created. This method might return null, if no
     * clip rectangles need to be added and the input argument has been null.
     *
     * @param clipRects the list of clip rectangles before adding new
     *        rectangles, possibly null
     *
     * @return the list of clip rectangles, possibly null
     */
    private List<Rectangle> addClipRects(List<Rectangle> clipRects) {
        LinkedList<ManagedWindow> siblings = parent.getChildren();
        if (siblings.getLast() != this) {
            if (clipRects == null) {
                clipRects = new LinkedList<Rectangle>();
                Iterator<ManagedWindow> i = siblings.descendingIterator();
                while (i.hasNext()) {
                    ManagedWindow sibling = i.next();
                    if (sibling == this) {
                        break;
                    }
                    if (sibling.isVisible()) {
                        Rectangle bounds = sibling.getBounds();
                        clipRects.add(bounds);
                    }
                }
            }
        }
        return clipRects;
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        return parent.getGraphicsConfiguration();
    }

    @Override
    public void dispose() {
        setVisible(false); // To trigger repaint.
        parent.remove(this);
    }

    @Override
    public Rectangle getBounds() {
        // Return new rectangle, so client code won't mess with our data.
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void setBounds(int x, int y, int width, int height, int op) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        // TODO: This needs to do more, like repainting, etc. Implement it.
    }

    @Override
    public Insets getInsets() {
        // No decorations yet -> no insets. Return new Insets always, so
        // client code can't mess with our data.
        return new Insets(0, 0, 0, 0);
    }

    @Override
    public Point getLocationOnScreen() {
        return parent.getLocationOnScreen(this);
    }

    @Override
    public Point getLocationOnScreen(ManagedWindow child) {
        Point myLoc = getLocationOnScreen();
        Rectangle childBounds = child.getBounds();
        return new Point(myLoc.x + childBounds.x, myLoc.y + childBounds.y);
    }

    @Override
    public boolean canDetermineObscurity() {
        // TODO: Implement this for real.
        return false;
    }

    @Override
    public boolean isObscured() {
        // TODO: Implement this for real.
        return false;
    }

    @Override
    public void applyShape(Region shape) {
        // No support for shaped windows.
    }

    @Override
    public boolean isReparentSuppored() {
        // TODO: Implement this for real.
        return false;
    }

    @Override
    public void reparent(ContainerPeer newContainer) {
        // TODO: Implement this for real.
    }

    @Override
    public boolean isRestackSupported() {
        // TODO: Implement this for real.
        return false;
    }

    @Override
    public void restack() {
        // TODO: Implement this for real.
    }

    @Override
    public void setVisible(boolean b) {
        visible = b;
        parent.setVisible(this, b);
    }

    @Override
    public void setVisible(ManagedWindow child, boolean v) {
        // We need to repaint ourselves and then call super to repaint the
        // children.
        Rectangle b = child.getBounds();
        CacioComponent cacioComp = getCacioComponent();
        Component awtComp = cacioComp.getAWTComponent();
        PaintEvent ev = new PaintEvent(awtComp, PaintEvent.PAINT, b);
        cacioComp.handlePeerEvent(ev);
        super.setVisible(child, v);
    }

    boolean isVisible() {
        return visible;
    }

    @Override
    public int getState() {
        // TODO: Implement this.
        return 0;
    }

    @Override
    public void setState(int state) {
        // TODO: Implement this.
    }

    @Override
    public void setMaximizedBounds(Rectangle bounds) {
        // TODO: Implement this.
    }

    @Override
    public void setResizable(boolean resizable) {
        // TODO: Implement this.
    }

    @Override
    public void setTitle(String title) {
        // TODO: Implement this.
    }

    @Override
    public boolean requestFocus(Component lightweightChild, boolean temporary,
                                boolean focusedWindowChangeAllowed, long time,
                                Cause cause) {
        // TODO: Implement this.
        return false;
    }

    CacioComponent getCacioComponent() {
        return cacioComponent;
    }

    public void dispatchEvent(AWTEvent event) {
        getCacioComponent().handlePeerEvent(event);
    }

    protected boolean dispatchEventImpl(EventData event) {
        // First try to dispatch to a child.
        boolean dispatched = super.dispatchEventImpl(event);
        if (! dispatched) {
            // If not dispatched, then dispatch to our own component.
            event.setSource(cacioComponent.getAWTComponent());
            cacioComponent.handlePeerEvent(event.createAWTEvent());
            dispatched = true;
        }
        return dispatched;
    }

    @Override
    public void setBackground(Color c) {
        background = c;
    }

    @Override
    public void setFont(Font f) {
        font = f;
    }

    @Override
    public void setForeground(Color c) {
        foreground = c;
    }

    @Override
    public void createBuffers(int numBuffers, BufferCapabilities caps)
        throws AWTException {

        // TODO: Implement this correctly.        
        throw new AWTException("Not yet supported.");
    }

    @Override
    public void destroyBuffers() {
        // Nothing to do here yet.        
    }

    @Override
    public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {
        // Nothing to do here yet.        
    }

    @Override
    public Image getBackBuffer() {
        // Nothing to do here yet.        
        return null;
    }
}