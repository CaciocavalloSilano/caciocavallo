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

package gnu.java.awt.peer.x;

import gnu.x11.Atom;
import gnu.x11.Drawable;
import gnu.x11.Window;
import gnu.x11.event.Event;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.awt.image.ColorModel;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;

import sun.awt.CausedFocusEvent.Cause;
import sun.awt.peer.cacio.CacioComponent;
import sun.awt.peer.cacio.PlatformWindow;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

class EscherPlatformWindow implements PlatformWindow {

    private static int standardSelect =
                        Event.BUTTON_PRESS_MASK
                        | Event.BUTTON_RELEASE_MASK | Event.POINTER_MOTION_MASK
                        // | Event.RESIZE_REDIRECT_MASK //
                        | Event.EXPOSURE_MASK | Event.PROPERTY_CHANGE_MASK
                        | Event.STRUCTURE_NOTIFY_MASK //
                        // | Event.SUBSTRUCTURE_NOTIFY_MASK
                        | Event.KEY_PRESS_MASK | Event.KEY_RELEASE_MASK
                        // | Event.VISIBILITY_CHANGE_MASK //
                        | Event.FOCUS_CHANGE_MASK
                        ;

    /**
     * The corresponding CacioCavallo component.
     */
    private CacioComponent cacioComponent;

    /**
     * The X window.
     */
    Window xwindow;

    /**
     * The surface data object for this window.
     */
    private SurfaceData surfaceData;

    /**
     * The frame insets. These get updated in {@link #show()}.
     */
    private Insets insets;

    EscherPlatformWindow(CacioComponent cacioComp, PlatformWindow parent) {

        cacioComponent = cacioComp;
        Component awtComp = cacioComp.getAWTComponent();
        XGraphicsDevice dev = EscherToolkit.getDefaultDevice();

        // TODO: Maybe initialize lazily in show().
        Window.Attributes atts = new Window.Attributes();

        // FIXME: Howto generate a Window without decorations?
        int x = Math.max(awtComp.getX(), 0);
        int y = Math.max(awtComp.getY(), 0);
        int w = Math.max(awtComp.getWidth(), 1);
        int h = Math.max(awtComp.getHeight(), 1);

        Window parentWindow = null;

        Insets parentInsets;
        if (parent == null) {
            parentWindow = dev.getDisplay().getRootWindow();
            parentInsets = new Insets(0, 0, 0, 0);
        } else {
            parentWindow = ((EscherPlatformWindow) parent).xwindow;
            parentInsets = parent.getInsets();
        }

        xwindow = new Window(parentWindow, x - parentInsets.left,
                             y - parentInsets.top, w, h, 0, atts);
        xwindow.select_input(standardSelect);

        dev.getEventPump().registerWindow(xwindow, cacioComp);
        xwindow.set_wm_delete_window();

        awtComp.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));

        boolean undecorated;
        if (awtComp instanceof Frame) {
            Frame f = (Frame) awtComp;
            undecorated = f.isUndecorated();
        } else if (awtComp instanceof Dialog) {
            Dialog d = (Dialog) awtComp;
            undecorated = d.isUndecorated();
        } else {
            // Everything else is undecorated by default.
            undecorated = true;
        }
        if (undecorated) {
            // First try the Motif implementation of undecorated frames. This
            // is semantically closest and supported by all major window
            // managers.
            // TODO: At the time of writing this, there's no freedesktop.org
            // standard extension that matches the required semantic. Maybe
            // undecorated frames are added in the future, if so, then use
            // these.
            Atom at = Atom.intern(dev.getDisplay(), "_MOTIF_WM_HINTS");
            if (at != null) {
                xwindow.change_property(Window.REPLACE, at, at, 32, new int[] {
                        1 << 1, 0, 0, 0, 0 }, 0, 5);
            }
        }
        // TODO: Get an estimate of the insets here by used the
        // _NET_FRAME_EXTENTS mechanism.
        insets = new Insets(0, 0, 0, 0);

        // Setup the surface data.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        surfaceData =
          new XDrawableSurfaceData((XGraphicsConfiguration) gc, xwindow,
                                   XDrawableSurfaceData.EscherIntRgb,
                                   getColorModel());

        if (awtComp.isVisible())
            show();
    }

    @Override
    public void applyShape(Region shape) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        XGraphicsDevice dev = EscherToolkit.getDefaultDevice();
        dev.getEventPump().unregisterWindow(xwindow);
    }

    @Override
    public Rectangle getBounds() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBounds(int x, int y, int width, int height, int op) {
        Insets i = insets;
        Insets parentInsets = getParentToplevelInsets();
        if (op == ComponentPeer.SET_BOUNDS) {
            xwindow.move_resize(x - i.left - parentInsets.left,
                    y - i.top - parentInsets.top,
                    width - i.left - i.right,
                    height - i.top - i.bottom);
        }
        else if (op == ComponentPeer.SET_LOCATION)
            xwindow.move(x - i.left - parentInsets.left,
                    y - i.top - parentInsets.top);
        else if (op == ComponentPeer.SET_SIZE)
            xwindow.resize(width - i.left - i.right,
                    height - i.top - i.bottom);
        else if (op == ComponentPeer.SET_CLIENT_SIZE)
            xwindow.resize(width, height);
    }

    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
    private Insets getParentToplevelInsets() {
        Insets insets;
        Component awtComp = cacioComponent.getAWTComponent();
        Component parent = awtComp.getParent();
        if (parent instanceof java.awt.Window) {
            insets = ((java.awt.Window) parent).getInsets();
        } else {
            insets = EMPTY_INSETS;
        }
        return insets;
    }

    @Override
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    @Override
    public Graphics2D getGraphics() {
        Component awtComponent = cacioComponent.getAWTComponent();
        Color fg = awtComponent.getForeground();
        if (fg == null)
            fg = Color.BLACK;
        Color bg = awtComponent.getBackground();
        if (bg == null)
            bg = Color.WHITE;
        SunGraphics2D sg2d = new SunGraphics2D(surfaceData, fg, bg,
                awtComponent.getFont());
        sg2d.translate(-insets.left, -insets.top);
        return sg2d;
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        // TODO Auto-generated method stub
        return new XGraphicsConfiguration(EscherToolkit.getDefaultDevice());
    }

    @Override
    public Insets getInsets() {
        return (Insets) insets.clone();
    }

    @Override
    public Point getLocationOnScreen() {
        Drawable.GeometryInfo geoInfo = xwindow.get_geometry();
        Point p = new Point(geoInfo.x - insets.left, geoInfo.y - insets.top);
        return p;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
           show(); 
        } else {
            hide();
        }
    }

    private void show()
    {
      // Prevent ResizeRedirect events.
      //xwindow.select_input(Event.NO_EVENT_MASK);
      //xwindow.select_input(noResizeRedirectSelect);

      XGraphicsDevice dev = EscherToolkit.getDefaultDevice();
      xwindow.map();
      EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
      Component awtComponent = cacioComponent.getAWTComponent();
      if (awtComponent instanceof java.awt.Window) {
          java.awt.Window w = (java.awt.Window) awtComponent;
          eq.postEvent(new WindowEvent(w, WindowEvent.WINDOW_OPENED));
      }
      eq.postEvent(new PaintEvent(awtComponent, PaintEvent.PAINT,
                                  new Rectangle(0, 0, awtComponent.getWidth(),
                                                awtComponent.getHeight())));
      Graphics g = getGraphics();
      g.clearRect(0, 0, awtComponent.getWidth(), awtComponent.getHeight());
      g.dispose();
//      // Reset input selection.
//      atts.set_override_redirect(false);
//      xwindow.change_attributes(atts);
      
      // Determine the frame insets.
      Atom atom = (Atom) Atom.intern(dev.getDisplay(), "_NET_FRAME_EXTENTS");
      Window.Property p = xwindow.get_property(false, atom, Atom.CARDINAL, 0,
                                               Window.MAX_WM_LENGTH);
      if (p.format() != 0)
        {
          insets = new Insets(p.value(2), p.value(0), p.value(3), p.value(1));
          Window.Changes ch = new Window.Changes();
          ch.width(awtComponent.getWidth() - insets.left - insets.top);
          ch.height(awtComponent.getHeight() - insets.top - insets.bottom);
          xwindow.configure(ch);
        }

    }

    /**
     * Makes the component invisible. This is called from
     * {@link Component#hide()}.
     *
     * This is implemented to call setVisible(false) on the Swing component.
     */
    private void hide()
    {
      xwindow.unmap();
    }

    @Override
    public boolean canDetermineObscurity() {
        return false;
    }

    @Override
    public boolean isObscured() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isReparentSuppored() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void reparent(ContainerPeer newContainer) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isRestackSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void restack() {
        // TODO Auto-generated method stub

    }

    @Override
    public int getState() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setMaximizedBounds(Rectangle bounds) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setResizable(boolean resizable) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setState(int state) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTitle(String title) {
        xwindow.set_wm_name (title);
    }

    public boolean requestFocus(Component lightweightChild, boolean temporary,
                                boolean focusedWindowChangeAllowed, long time,
                                Cause cause) {

        xwindow.set_input_focus();

        return true;
    }

    @Override
    public void updateCursorImmediately() {
        System.err.println("IMPLEMENT ME: EscherPlatformWindow.updateCursorImmediately()");
    }
}
