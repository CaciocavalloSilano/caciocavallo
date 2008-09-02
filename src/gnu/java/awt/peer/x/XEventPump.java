/* XEventPump.java
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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import sun.awt.AWTAutoShutdown;
import sun.awt.event.ComponentReshapeEvent;
import sun.awt.peer.cacio.CacioComponent;

import gnu.x11.Display;
import gnu.x11.event.ButtonPress;
import gnu.x11.event.ButtonRelease;
import gnu.x11.event.ClientMessage;
import gnu.x11.event.ConfigureNotify;
import gnu.x11.event.DestroyNotify;
import gnu.x11.event.Event;
import gnu.x11.event.Expose;
import gnu.x11.event.Input;
import gnu.x11.event.KeyPress;
import gnu.x11.event.KeyRelease;
import gnu.x11.event.MotionNotify;
import gnu.x11.event.PropertyNotify;

/**
 * Fetches events from X, translates them to AWT events and pumps them up
 * into the AWT event queue.
 *
 * @author Roman Kennke (kennke@aicas.com)
 */
public class XEventPump
  implements Runnable
{

  /**
   * The X Display from which we fetch and pump up events.
   */
  private Display display;

  /**
   * Maps X Windows to AWT Windows to be able to correctly determine the
   * event targets.
   */
  private HashMap<Integer,CacioComponent> windows;

  /**
   * Indicates if we are currently inside a drag operation. This is
   * set to the button ID when a button is pressed and to -1 (indicating
   * that no drag is active) when the mouse is released.
   */
  private int drag;

  /**
   * Creates a new XEventPump for the specified X Display.
   *
   * @param d the X Display
   */
  XEventPump(Display d)
  {
    display = d;
    windows = new HashMap<Integer,CacioComponent>();
    drag = -1;
    Thread thread = new Thread(this, "X Event Pump");
    thread.setDaemon(true);
    thread.start();
  }

  /**
   * The main event pump loop. This basically fetches events from the
   * X Display and pumps them into the system event queue.
   */
  public void run()
  {
    while (display.connected)
      {
        try
          {
            //AWTAutoShutdown.notifyToolkitThreadFree();
            Event xEvent = display.next_event();
            AWTAutoShutdown.notifyToolkitThreadBusy();
            handleEvent(xEvent);
          }
        catch (ThreadDeath death)
          {
            // If someone wants to kill us, let them.
            return;
          }
        catch (Throwable x)
          {
            System.err.println("Exception during event dispatch:");
            x.printStackTrace(System.err);
          }
      }
  }

  /**
   * Adds an X Window to AWT Window mapping. This is required so that the
   * event pump can correctly determine the event targets.
   *
   * @param xWindow the X Window
   * @param awtWindow the AWT Window
   */
  void registerWindow(gnu.x11.Window xWindow, CacioComponent cacioComponent)
  {
    if (EscherToolkit.DEBUG)
      System.err.println("registering window id: " + xWindow.id);
    windows.put(new Integer(xWindow.id), cacioComponent);
  }

  void unregisterWindow(gnu.x11.Window xWindow)
  {
    windows.remove(new Integer(xWindow.id));
  }

  private void handleButtonPress(ButtonPress event)
  {
    Integer key = new Integer(event.getEventWindowID());
    CacioComponent cacioComponent = windows.get(key);

    // Create and post the mouse event.
    int button = event.detail();

    // AWT cannot handle more than 3 buttons and expects 0 instead.
    if (button >= gnu.x11.Input.BUTTON3)
      button = 0;
    drag = button;

    Insets i = getComponentInsets(cacioComponent);
    Component awtComponent = cacioComponent.getAWTComponent();
    MouseEvent mp = new MouseEvent(awtComponent, MouseEvent.MOUSE_PRESSED,
                                   System.currentTimeMillis(),
                                   KeyboardMapping.mapModifiers(event.getState())
                                     | buttonToModifier(button),
                                   event.getEventX() + i.left,
                                   event.getEventY() + i.top,
                                   1, false, button);
    postEvent(cacioComponent, mp);
  }
  
  private void handleButtonRelease(ButtonRelease event)
  {
    Integer key = new Integer(event.getEventWindowID());
    CacioComponent cacioComponent = windows.get(key);
    Component awtComp = cacioComponent.getAWTComponent();

    int button = event.detail();
    
    // AWT cannot handle more than 3 buttons and expects 0 instead.
    if (button >= gnu.x11.Input.BUTTON3)
      button = 0;
    drag = -1;
    
    Insets i = getComponentInsets(cacioComponent);
    MouseEvent mr = new MouseEvent(awtComp, MouseEvent.MOUSE_RELEASED,
                                   System.currentTimeMillis(),
                                   KeyboardMapping.mapModifiers(event.getState())
                                     | buttonToModifier(button),
                                   event.getEventX() + i.left,
                                   event.getEventY() + i.top,
                                   1, false, button);
    postEvent(cacioComponent, mr);
  }
  
  private void handleMotionNotify(MotionNotify event)
  {
    Integer key = new Integer(event.getEventWindowID());
    CacioComponent cacioComponent = windows.get(key);
    Component awtComponent = cacioComponent.getAWTComponent();

    int button = event.detail();
    
    // AWT cannot handle more than 3 buttons and expects 0 instead.
    if (button >= gnu.x11.Input.BUTTON3)
      button = 0;

    MouseEvent mm = null;
    Insets i = getComponentInsets(cacioComponent);

    if (drag == -1)
      {
        mm = new MouseEvent(awtComponent, MouseEvent.MOUSE_MOVED,
                            System.currentTimeMillis(),
                            KeyboardMapping.mapModifiers(event.getState())
                              | buttonToModifier(button),
                            event.getEventX() + i.left,
                            event.getEventY() + i.top,
                            1, false);

      }
    else
      {
        mm = new MouseEvent(awtComponent, MouseEvent.MOUSE_DRAGGED,
                            System.currentTimeMillis(),
                            KeyboardMapping.mapModifiers(event.getState())
                              | buttonToModifier(drag),
                            event.getEventX(), event.getEventY(),
                            1, false);
      }
    postEvent(cacioComponent, mm);
  }
  
  private void clearWindow(CacioComponent cacioComponent, int x, int y, int w, int h)
  {
    Component awtComponent = cacioComponent.getAWTComponent();

    // Tell the AWT component about the new bounds without triggering any
    // layout or event activity.
    int compX = awtComponent.getX();
    int compY = awtComponent.getY();
    int compW = awtComponent.getWidth();
    int compH = awtComponent.getHeight();
    Insets i = getComponentInsets(cacioComponent);
    boolean resized = false;
    boolean moved = false;
    if (compX + i.left != x || compY + i.top != y) {
        moved = true;
    }
    if (compW - i.left - i.right != w || compH - i.top - i.bottom != h) {
        resized = true;
    }

    if (resized || moved) {
        ComponentReshapeEvent cre =
            new ComponentReshapeEvent(awtComponent, x - i.left, y - i.top,
                                      w + i.left + i.right,
                                      h + i.top + i.bottom);
        postEvent(cacioComponent, cre);

        ComponentEvent ce;
        if (resized) {
            ce = new ComponentEvent(awtComponent,
                                    ComponentEvent.COMPONENT_RESIZED);
            postEvent(cacioComponent, ce);
        }
        if (moved) {
            ce = new ComponentEvent(awtComponent,
                                    ComponentEvent.COMPONENT_RESIZED);
            postEvent(cacioComponent, ce);
        }

        Rectangle r = awtComponent.getBounds();
        PaintEvent pev = new PaintEvent(awtComponent, PaintEvent.UPDATE, r);
        postEvent(cacioComponent, pev);
    
    }

  }
  
  private void handleConfigureNotify(ConfigureNotify event)
  {
    Integer key = new Integer(event.window_id);
    CacioComponent cacioComponent = windows.get(key);
    Component awtComponent = cacioComponent.getAWTComponent();

    if (EscherToolkit.DEBUG)
      System.err.println("resize request for window id: " + key);

    // Detect and report size changes.
    clearWindow(cacioComponent, event.x(), event.y(), event.width(), event.height());

  }
  
  // FIME: refactor and make faster, maybe caching the event and handle
  // and/or check timing (timing is generated for PropertyChange)?
  private void handleExpose(Expose event)
  {
    Integer key = new Integer(event.window_id);
    CacioComponent cacioComponent = windows.get(key);
    Component awtComponent = cacioComponent.getAWTComponent();

    if (EscherToolkit.DEBUG)
      System.err.println("expose request for window id: " + key);
    
    Rectangle r = new Rectangle(event.x(), event.y(), event.width(),
                                event.height());
    // We need to clear the background of the exposed rectangle.
    assert awtComponent != null : "awtWindow == null for window ID: " + key;
     
    Graphics g = awtComponent.getGraphics();
    g.clearRect(r.x, r.y, r.width, r.height);
    g.dispose();

    PaintEvent pev = new PaintEvent(awtComponent, PaintEvent.UPDATE, r);
    postEvent(cacioComponent, pev);
  }
    
  private void handleDestroyNotify(DestroyNotify destroyNotify)
  {
    if (EscherToolkit.DEBUG)
      System.err.println("DestroyNotify event: " + destroyNotify);
    
    Integer key = new Integer(destroyNotify.event_window_id);
    CacioComponent cacioComponent = windows.get(key);
    Component awtComp = cacioComponent.getAWTComponent();
    if (awtComp instanceof Window) {
        AWTEvent event = new WindowEvent((Window) awtComp,
                                         WindowEvent.WINDOW_CLOSED);
        postEvent(cacioComponent, event);
    }
  }
  
  private void handleClientMessage(ClientMessage clientMessage)
  {
    if (EscherToolkit.DEBUG)
      System.err.println("ClientMessage event: " + clientMessage);
    
    if (clientMessage.delete_window())
      {
        if (EscherToolkit.DEBUG)
          System.err.println("ClientMessage is a delete_window event");
        
        Integer key = new Integer(clientMessage.window_id);
        CacioComponent cacioComponent = windows.get(key);
        Component awtComp = cacioComponent.getAWTComponent();
        if (awtComp instanceof Window) {
            AWTEvent event = new WindowEvent((Window) awtComp,
                                             WindowEvent.WINDOW_CLOSING);
            postEvent(cacioComponent, event);
        }
      }
  }
    
  private void handleEvent(Event xEvent)
  {
    Integer key = null;
    CacioComponent cacioComponent = null;

    if (EscherToolkit.DEBUG)
      System.err.println("fetched event: " + xEvent);
    
    switch (xEvent.code() & 0x7f)
    {
    case ButtonPress.CODE:
      this.handleButtonPress((ButtonPress) xEvent);
      break;
    case ButtonRelease.CODE:
      this.handleButtonRelease((ButtonRelease) xEvent); 
      break;
    case MotionNotify.CODE:
      this.handleMotionNotify((MotionNotify) xEvent);
      break;
    case Expose.CODE:
      this.handleExpose((Expose) xEvent);
      break;
    case KeyPress.CODE:
    case KeyRelease.CODE:
      key = new Integer(((Input) xEvent).getEventWindowID());
      cacioComponent = windows.get(key);
      handleKeyEvent(xEvent, cacioComponent);
      break;
    case DestroyNotify.CODE:
      this.handleDestroyNotify((DestroyNotify) xEvent);
      break;
    case ClientMessage.CODE:
      this.handleClientMessage((ClientMessage) xEvent);
      break;
    case PropertyNotify.CODE:
      key = new Integer (((PropertyNotify) xEvent).getWindowID());
      cacioComponent = windows.get(key);
      Component awtComponent = cacioComponent.getAWTComponent();
      if (awtComponent instanceof Window) {
          AWTEvent event = new WindowEvent((Window) awtComponent,
                                           WindowEvent.WINDOW_STATE_CHANGED);
          postEvent(cacioComponent, event);
      }
      break;
    case ConfigureNotify.CODE:
      this.handleConfigureNotify((ConfigureNotify) xEvent);
      break;
    default:
      if (EscherToolkit.DEBUG)
        System.err.println("Unhandled X event: " + xEvent);
    }
  }

  /**
   * Handles key events from X.
   *
   * @param xEvent the X event
   * @param awtWindow the AWT window to which the event gets posted
   */
  private void handleKeyEvent(Event xEvent, CacioComponent cacioComponent)
  {
    Component awtComponent = cacioComponent.getAWTComponent();
    Input keyEvent = (Input) xEvent;
    int xKeyCode = keyEvent.detail();
    int xMods = keyEvent.getState();
    int keyCode = KeyboardMapping.mapToKeyCode(xEvent.display.input, xKeyCode,
                                               xMods);
    char keyChar = KeyboardMapping.mapToKeyChar(xEvent.display.input, xKeyCode,
                                                xMods);
    if (EscherToolkit.DEBUG)
      System.err.println("XEventPump.handleKeyEvent: " + xKeyCode + ", "
                         + xMods + ": " + ((int) keyChar) + ", " + keyCode);
    int awtMods = KeyboardMapping.mapModifiers(xMods);
    long when = System.currentTimeMillis();
    KeyEvent ke;
    if (keyEvent.code() == KeyPress.CODE)
      {
        ke = new KeyEvent(awtComponent, KeyEvent.KEY_PRESSED, when,
                          awtMods, keyCode,
                          KeyEvent.CHAR_UNDEFINED);
        postEvent(cacioComponent, ke);
        if (keyChar != KeyEvent.CHAR_UNDEFINED)
          {
            ke = new KeyEvent(awtComponent, KeyEvent.KEY_TYPED, when,
                              awtMods, KeyEvent.VK_UNDEFINED,
                              keyChar);
            postEvent(cacioComponent, ke);
          }
          
      }
    else
      {
        ke = new KeyEvent(awtComponent, KeyEvent.KEY_RELEASED, when,
                          awtMods, keyCode,
                          KeyEvent.CHAR_UNDEFINED);
        postEvent(cacioComponent, ke);
      }

  }

  /** Translates an X button identifier to the AWT's MouseEvent modifier
   *  mask. As the AWT cannot handle more than 3 buttons those return
   *  <code>0</code>.
   */
  static int buttonToModifier(int button)
  {
    switch (button)
    {
      case gnu.x11.Input.BUTTON1:
        return MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON1_MASK;
      case gnu.x11.Input.BUTTON2:
        return MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON2_MASK;
      case gnu.x11.Input.BUTTON3:
        return MouseEvent.BUTTON3_DOWN_MASK | MouseEvent.BUTTON3_MASK;
    }

    return 0;        
  }

  private void postEvent(CacioComponent cacioComponent, AWTEvent ev)
  {
      cacioComponent.handlePeerEvent(ev);
  }

  private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

  private Insets getComponentInsets(CacioComponent cacioComp) {
      Insets insets;
      Component awtComp = cacioComp.getAWTComponent();
      if (awtComp instanceof Container) {
          insets = ((Container) awtComp).getInsets();
      } else {
          insets = EMPTY_INSETS;
      }
      return insets;
  }
}
