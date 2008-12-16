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
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.PaintEvent;
import java.awt.geom.Area;
import java.awt.peer.ContainerPeer;
import java.awt.image.ColorModel;
import java.util.Iterator;
import java.util.LinkedList;

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
        System.err.println("create new ManagedWindow with parent: " + p + " and AWT component: " + c);
        setBounds(c.getX(), c.getY(), c.getWidth(), c.getHeight(), 0);
    }

    @Override
    public ColorModel getColorModel() {
        return parent.getColorModel();
    }

    @Override
    public Graphics2D getGraphics() {
        Graphics2D pg = parent.getGraphics();
        // Clip away sibling's areas that overlap.
        LinkedList<ManagedWindow> siblings = parent.getChildren();
        // We only need to do this if the uppermost sibling is
        // something different than this window.
        if (siblings.getLast() != this) {
            Rectangle b = parent.getBounds();
            Area clip = new Area(new Rectangle(0, 0, b.width, b.height));
            Iterator<ManagedWindow> i = siblings.descendingIterator();
            while (i.hasNext()) {
                ManagedWindow s = i.next();
                if (s == this) {
                    break;
                }
                if (s.isVisible()) {
                    Area sibArea = new Area(s.getBounds());
                    clip.subtract(sibArea);
                }
            }
            pg.clip(clip);
        }

        // Clip and translate to this window.
        pg = (Graphics2D) pg.create(x, y, width, height);
        return pg;
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

    @Override
    public void updateCursorImmediately() {
        // TODO: Implement this.
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
}