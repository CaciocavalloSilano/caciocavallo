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

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.PaintEvent;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import sun.awt.peer.cacio.CacioComponent.EventPriority;

/**
 * A base implementation of {@link ManagedWindowContainer}. This can be
 * used as a basis for PlatformWindow implementations that are aimed
 * to implement toplevel windows only, and use ManagedWindow instances
 * for nested windows.
 */
public abstract class AbstractManagedWindowContainer
    implements ManagedWindowContainer {

    /**
     * The child windows of this container.
     */
    private LinkedList<ManagedWindow> children;

    /**
     * Constructs a new instance of AbstractManagedWindowContainer that
     * uses the specified parent container.
     */
    protected AbstractManagedWindowContainer() {
        children = new LinkedList<ManagedWindow>();
    }

    /**
     * Adds a child window to this container. This will be the topmost
     * window in the stack.
     *
     * @param child the window to add
     */
    @Override
    public void add(ManagedWindow child) {
        children.add(child);
        // TODO: This needs to do a little more in reality (repaint, etc).
        // Fix it.
    }

    /**
     * Removes a child window from this container.
     *
     * @param child the window to be removed
     */
    @Override
    public void remove(ManagedWindow child) {
        children.remove(child);
        // TODO: This needs to do a little more in reality (repaint, etc).
        // Fix it.
    }

    @Override
    public LinkedList<ManagedWindow> getChildren() {
        return children;
    }

    /**
     * Returns the location of the specified child window on screen.
     *
     * The default implementation of this method assumes we are a toplevel
     * window and simply returns the location of the child window relative
     * to ourselves.
     */
    @Override
    public Point getLocationOnScreen() {
        return new Point(0, 0);
    }

    @Override
    public void dispatchEvent(EventData event) {
        dispatchEventImpl(event);
    }

    protected boolean dispatchEventImpl(EventData event) {
        int id = event.getId();
        if (id >= MouseEvent.MOUSE_FIRST
            && id <= MouseEvent.MOUSE_LAST) {
            ManagedWindow source = findWindowAt(event.getX(), event.getY());
            if (source != null) {
                event.setSource(source);
                Rectangle b = source.getBounds();
                event.setX(event.getX() - b.x);
                event.setY(event.getY() - b.y);
                return source.dispatchEventImpl(event);
            } else {
                return false;
            }
        } else if (id >= KeyEvent.KEY_FIRST && id <= KeyEvent.KEY_LAST) {
            FocusManager fm = FocusManager.getInstance();
            ManagedWindow window = fm.getFocusedWindow();
            if (window != null) {
                window.dispatchKeyEvent(event);
            }
            return true;
        } else {
            return false;
        }
    }

    protected ManagedWindow findWindowAt(int x, int y) {
        // Search from topmost to bottommost component and see if one matches.
        Iterator<ManagedWindow> i = children.descendingIterator();
        while (i.hasNext()) {
            ManagedWindow child = i.next();
            if (child.isVisible()) {
                Rectangle b = child.getBounds();
                if (x >= b.x && y >= b.y
                    && x < (b.x + b.width) && y < (b.y + b.height)) {
                    return child;
                }
            }
        }
        // If we reach here, we found no child at those coordinates.
        return null;
    }

    @Override
    public void setVisible(ManagedWindow child, boolean v) {
        if (! v) {
            // TODO: Lots of optimization potential here!
            Rectangle rect = child.getBounds();
            Rectangle intersect = new Rectangle();

            // Send paint events to repaint stuff 'behind' closed window.
            // Paint from bottommost to topmost component.
            Iterator<ManagedWindow> i = children.iterator();
            while (i.hasNext()) {
                ManagedWindow w = i.next();
                if (w.isVisible()) {
                    Rectangle b = w.getBounds();
                    Rectangle2D.intersect(rect, b, intersect);
                    if (! intersect.isEmpty()) {
                        CacioComponent cacioComp = w.getCacioComponent();
                        Component awtComp = cacioComp.getAWTComponent();
                        // We need to be relative to the target.
                        intersect.x -= b.x;
                        intersect.y -= b.y;
                        PaintEvent ev = new PaintEvent(awtComp,
                                                       PaintEvent.PAINT,
                                                       intersect);
                        cacioComp.handlePeerEvent(ev,
                                                  EventPriority.ULTIMATE);
                    }
                }
            }
        }
    }

    @Override
    public void repaint(int x, int y, int w, int h) {
        // Repaint the correct rectangles for all visible children that
        // are inside this rectangle.
        Iterator<ManagedWindow> i = children.descendingIterator();
        Rectangle rect = new Rectangle(x, y, w, h);
        Rectangle intersect = new Rectangle();
        while (i.hasNext()) {
            ManagedWindow child = i.next();
            if (child.isVisible()) {
                Rectangle b = child.getBounds();
                Rectangle2D.intersect(b, rect, intersect);
                if (! intersect.isEmpty()) {
                    CacioComponent cacioComp = child.getCacioComponent();
                    Component awtComp = cacioComp.getAWTComponent();
                    // We need to be relative to the target.
                    Rectangle area = new Rectangle(intersect);
                    area.x -= b.x;
                    area.y -= b.y;
                    PaintEvent ev = new PaintEvent(awtComp,
                                                   PaintEvent.PAINT,
                                                   area);
                    cacioComp.handlePeerEvent(ev,
                                              EventPriority.ULTIMATE);
                }
            }
        }
    }
}
