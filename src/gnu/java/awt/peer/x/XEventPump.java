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
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import sun.awt.AWTAutoShutdown;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

import gnu.java.util.ComponentReshapeEvent;
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
  private HashMap windows;

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
    windows = new HashMap();
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
  void registerWindow(gnu.x11.Window xWindow, Window awtWindow)
  {
    if (XToolkit.DEBUG)
      System.err.println("registering window id: " + xWindow.id);
    windows.put(new Integer(xWindow.id), awtWindow);
  }

  void unregisterWindow(gnu.x11.Window xWindow)
  {
    windows.remove(new Integer(xWindow.id));
  }

  private void handleButtonPress(ButtonPress event)
  {
    Integer key = new Integer(event.getEventWindowID());
    Window awtWindow = (Window) windows.get(key);

    // Create and post the mouse event.
    int button = event.detail();

    // AWT cannot handle more than 3 buttons and expects 0 instead.
    if (button >= gnu.x11.Input.BUTTON3)
      button = 0;
    drag = button;

    Insets i = awtWindow.getInsets();
    MouseEvent mp = new MouseEvent(awtWindow, MouseEvent.MOUSE_PRESSED,
                                   System.currentTimeMillis(),
                                   KeyboardMapping.mapModifiers(event.getState())
                                     | buttonToModifier(button),
                                   event.getEventX() + i.left,
                                   event.getEventY() + i.top,
                                   1, false, button);
    postEvent(mp);
  }
  
  private void handleButtonRelease(ButtonRelease event)
  {
    Integer key = new Integer(event.getEventWindowID());
    Window awtWindow = (Window) windows.get(key);

    int button = event.detail();
    
    // AWT cannot handle more than 3 buttons and expects 0 instead.
    if (button >= gnu.x11.Input.BUTTON3)
      button = 0;
    drag = -1;
    
    Insets i = awtWindow.getInsets();
    MouseEvent mr = new MouseEvent(awtWindow, MouseEvent.MOUSE_RELEASED,
                                   System.currentTimeMillis(),
                                   KeyboardMapping.mapModifiers(event.getState())
                                     | buttonToModifier(button),
                                   event.getEventX() + i.left,
                                   event.getEventY() + i.top,
                                   1, false, button);
    postEvent(mr);
  }
  
  private void handleMotionNotify(MotionNotify event)
  {
    Integer key = new Integer(event.getEventWindowID());
    Window awtWindow = (Window) windows.get(key);

    int button = event.detail();
    
    // AWT cannot handle more than 3 buttons and expects 0 instead.
    if (button >= gnu.x11.Input.BUTTON3)
      button = 0;

    MouseEvent mm = null;
    Insets i = awtWindow.getInsets();
    if (drag == -1)
      {
        mm = new MouseEvent(awtWindow, MouseEvent.MOUSE_MOVED,
                            System.currentTimeMillis(),
                            KeyboardMapping.mapModifiers(event.getState())
                              | buttonToModifier(button),
                            event.getEventX() + i.left,
                            event.getEventY() + i.top,
                            1, false);

      }
    else
      {
        mm = new MouseEvent(awtWindow, MouseEvent.MOUSE_DRAGGED,
                            System.currentTimeMillis(),
                            KeyboardMapping.mapModifiers(event.getState())
                              | buttonToModifier(drag),
                            event.getEventX(), event.getEventY(),
                            1, false);
      }
    postEvent(mm);
  }
  
  private void clearWindow(Window awtWindow, int x, int y, int w, int h)
  {
    XWindowPeer xwindow = (XWindowPeer) awtWindow.getPeer();
    Insets i = xwindow.insets();
    if (w != awtWindow.getWidth() - i.left - i.right
        || h != awtWindow.getHeight() - i.top - i.bottom)
      {
        
        if (XToolkit.DEBUG)
          System.err.println("Setting size on AWT window: " + w
                           + ", " + h + ", " + awtWindow.getWidth()
                           + ", " + awtWindow.getHeight());
        
        // new width and height
        xwindow.callback = true;
        xwindow.xwindow.width = w;
        xwindow.xwindow.height = h;
        awtWindow.setBounds(x - i.left, y - i.top, w + i.left + i.right,
                            h + i.top + i.bottom);
        xwindow.callback = false;
        
        // reshape the window
        ComponentReshapeEvent cre =
          new ComponentReshapeEvent(awtWindow, x, y, w, h);
        awtWindow.dispatchEvent(cre);
      }
    
    ComponentEvent ce =
      new ComponentEvent(awtWindow, ComponentEvent.COMPONENT_RESIZED);
    postEvent(ce);
  }
  
  private void handleConfigureNotify(ConfigureNotify event)
  {
    Integer key = new Integer(event.window_id);
    Window awtWindow = (Window) windows.get(key);
   
    if (XToolkit.DEBUG)
      System.err.println("resize request for window id: " + key);

    // Detect and report size changes.
    this.clearWindow(awtWindow, event.x(), event.y(), event.width(), event.height());

    Rectangle r = awtWindow.getBounds();
    
    ComponentEvent ce =
      new ComponentEvent(awtWindow, ComponentEvent.COMPONENT_RESIZED);
    postEvent(ce);
    
    PaintEvent pev = new PaintEvent(awtWindow, PaintEvent.UPDATE, r);
    postEvent(pev);
  }
  
  // FIME: refactor and make faster, maybe caching the event and handle
  // and/or check timing (timing is generated for PropertyChange)?
  private void handleExpose(Expose event)
  {
    Integer key = new Integer(event.window_id);
    Window awtWindow = (Window) windows.get(key);
    
    if (XToolkit.DEBUG)
      System.err.println("expose request for window id: " + key);
    
    Rectangle r = new Rectangle(event.x(), event.y(), event.width(),
                                event.height());
    // We need to clear the background of the exposed rectangle.
    assert awtWindow != null : "awtWindow == null for window ID: " + key;
     
    Graphics g = awtWindow.getGraphics();
    g.clearRect(r.x, r.y, r.width, r.height);
    g.dispose();
    
    this.clearWindow(awtWindow, event.x(), event.y(), event.width(), event.height());
  
    ComponentEvent ce =
      new ComponentEvent(awtWindow, ComponentEvent.COMPONENT_RESIZED);
    postEvent(ce);
    
    PaintEvent pev = new PaintEvent(awtWindow, PaintEvent.UPDATE, r);
    postEvent(pev);
  }
    
  private void handleDestroyNotify(DestroyNotify destroyNotify)
  {
    if (XToolkit.DEBUG)
      System.err.println("DestroyNotify event: " + destroyNotify);
    
    Integer key = new Integer(destroyNotify.event_window_id);
    Window awtWindow = (Window) windows.get(key);
    
    AWTEvent event = new WindowEvent(awtWindow, WindowEvent.WINDOW_CLOSED);
    postEvent(event);
  }
  
  private void handleClientMessage(ClientMessage clientMessage)
  {
    if (XToolkit.DEBUG)
      System.err.println("ClientMessage event: " + clientMessage);
    
    if (clientMessage.delete_window())
      {
        if (XToolkit.DEBUG)
          System.err.println("ClientMessage is a delete_window event");
        
        Integer key = new Integer(clientMessage.window_id);
        Window awtWindow = (Window) windows.get(key);
        
        AWTEvent event = new WindowEvent(awtWindow, WindowEvent.WINDOW_CLOSING);
        postEvent(event);
      }
  }
    
  private void handleEvent(Event xEvent)
  {
    Integer key = null;
    Window awtWindow = null;
    
    if (XToolkit.DEBUG)
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
      awtWindow = (Window) windows.get(key);
      handleKeyEvent(xEvent, awtWindow);
      break;
    case DestroyNotify.CODE:
      this.handleDestroyNotify((DestroyNotify) xEvent);
      break;
    case ClientMessage.CODE:
      this.handleClientMessage((ClientMessage) xEvent);
      break;
    case PropertyNotify.CODE:
      key = new Integer (((PropertyNotify) xEvent).getWindowID());
      awtWindow = (Window) windows.get(key);
      AWTEvent event = new WindowEvent(awtWindow, WindowEvent.WINDOW_STATE_CHANGED);
      postEvent(event);
      break;
    case ConfigureNotify.CODE:
      this.handleConfigureNotify((ConfigureNotify) xEvent);
      break;
    default:
      if (XToolkit.DEBUG)
        System.err.println("Unhandled X event: " + xEvent);
    }
  }

  /**
   * Handles key events from X.
   *
   * @param xEvent the X event
   * @param awtWindow the AWT window to which the event gets posted
   */
  private void handleKeyEvent(Event xEvent, Window awtWindow)
  {
    Input keyEvent = (Input) xEvent;
    int xKeyCode = keyEvent.detail();
    int xMods = keyEvent.getState();
    int keyCode = KeyboardMapping.mapToKeyCode(xEvent.display.input, xKeyCode,
                                               xMods);
    char keyChar = KeyboardMapping.mapToKeyChar(xEvent.display.input, xKeyCode,
                                                xMods);
    if (XToolkit.DEBUG)
      System.err.println("XEventPump.handleKeyEvent: " + xKeyCode + ", "
                         + xMods + ": " + ((int) keyChar) + ", " + keyCode);
    int awtMods = KeyboardMapping.mapModifiers(xMods);
    long when = System.currentTimeMillis();
    KeyEvent ke;
    if (keyEvent.code() == KeyPress.CODE)
      {
        ke = new KeyEvent(awtWindow, KeyEvent.KEY_PRESSED, when,
                          awtMods, keyCode,
                          KeyEvent.CHAR_UNDEFINED);
        postEvent(ke);
        if (keyChar != KeyEvent.CHAR_UNDEFINED)
          {
            ke = new KeyEvent(awtWindow, KeyEvent.KEY_TYPED, when,
                              awtMods, KeyEvent.VK_UNDEFINED,
                              keyChar);
            postEvent(ke);
          }
          
      }
    else
      {
        ke = new KeyEvent(awtWindow, KeyEvent.KEY_RELEASED, when,
                          awtMods, keyCode,
                          KeyEvent.CHAR_UNDEFINED);
        postEvent(ke);
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

  private void postEvent(AWTEvent ev)
  {
    SunToolkit.postEvent(AppContext.getAppContext(), ev);
  }
}
