/* XFramePeer.java
   Copyright (C) 2008 Mario Torre and Roman Kennke

This file is part of the Caciocavallo project.

Caciocavallo is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

Caciocavallo is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with Caciocavallo; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package gnu.java.awt.peer.x;

import gnu.x11.Atom;
import gnu.x11.Window.WMHints;

import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.peer.FramePeer;

public class XFramePeer
  extends XWindowPeer
  implements FramePeer
{
  /* FIXME: package visible? Do we need? */
  int state = 0;
  
  XFramePeer(Frame f)
  {
    super(f);
    setTitle(f.getTitle());
  }

  public void setIconImage(Image image)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  public void setMenuBar(MenuBar mb)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  public void setResizable(boolean resizable)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  public void setTitle(String title)
  {
    xwindow.set_wm_name (title);
  }

  public int getState()
  {
    return this.state;
  }

  public void setState(int state)
  {
    this.state = state;
    
    // TODO: state change to take effect
  }

  public void setMaximizedBounds(Rectangle r)
  {
    if (r == null)
      return;
    
    // TODO: set WM hints
    // xwindow.change_property(Atom.WM_SIZE_HINTS, Atom.WM_SIZE_HINTS, );
  }
  
  /**
   * Check if this frame peer supports being restacked.
   * 
   * @return true if this frame peer can be restacked,
   * false otherwise
   * @since 1.5
   */
  public boolean isRestackSupported()
  {
    return false;
  }
  
  /**
   * Sets the bounds of this frame peer.
   * 
   * @param x the new x co-ordinate
   * @param y the new y co-ordinate
   * @param width the new width
   * @param height the new height
   * @since 1.5
   */
  public void setBoundsPrivate(int x, int y, int width, int height)
  {
    // TODO: Implement this.
    throw new UnsupportedOperationException("Not yet implemented.");
  }

  public Rectangle getBoundsPrivate()
  {
    // TODO: Implement this properly.
    throw new InternalError("Not yet implemented");
  }

}
