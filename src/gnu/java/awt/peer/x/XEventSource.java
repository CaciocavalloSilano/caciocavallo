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
import java.util.LinkedList;

import sun.awt.AWTAutoShutdown;
import sun.awt.peer.cacio.CacioComponent;
import sun.awt.peer.cacio.CacioEventSource;
import sun.awt.peer.cacio.EventData;

import gnu.x11.Display;
import gnu.x11.event.ButtonPress;
import gnu.x11.event.ButtonRelease;
import gnu.x11.event.ClientMessage;
import gnu.x11.event.ConfigureNotify;
import gnu.x11.event.DestroyNotify;
import gnu.x11.event.Event;
import gnu.x11.event.Expose;
import gnu.x11.event.FocusIn;
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
public class XEventSource implements CacioEventSource
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
   * The (reused) event data object.
   */
  private EventData data;

  /**
   * Events waiting to be dispatched.
   */
  private LinkedList<EventData> waitingEvents;

  /**
   * Creates a new XEventPump for the specified X Display.
   *
   * @param d the X Display
   */
  XEventSource(Display d)
  {
    display = d;
    windows = new HashMap<Integer,CacioComponent>();
    drag = -1;
    data = new EventData();
    waitingEvents = new LinkedList<EventData>();
  }

  @Override
  public EventData getNextEvent() {
 
      if (waitingEvents.size() > 0) {
          return waitingEvents.removeFirst();
      }

      // TODO Auto-generated method stub
      Event xEvent = display.next_event();
      handleEvent(xEvent);
      return data;
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
    data.setSource(awtComponent);
    data.setId(MouseEvent.MOUSE_PRESSED);
    data.setTime(System.currentTimeMillis());
    data.setModifiers(KeyboardMapping.mapModifiers(event.getState())
                      | buttonToModifier(button));
    data.setX(event.getEventX() + i.left);
    data.setY(event.getEventY() + i.top);
    data.setClickCount(1);
    data.setButton(button);
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
    data.setSource(awtComp);
    data.setId(MouseEvent.MOUSE_RELEASED);
    data.setTime(System.currentTimeMillis());
    data.setModifiers(KeyboardMapping.mapModifiers(event.getState())
                      | buttonToModifier(button));
    data.setX(event.getEventX() + i.left);
    data.setY(event.getEventY() + i.top);
    data.setClickCount(1);
    data.setButton(button);
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

    data.setSource(awtComponent);
    data.setTime(System.currentTimeMillis());
    data.setModifiers(KeyboardMapping.mapModifiers(event.getState())
            | buttonToModifier(button));
    data.setX(event.getEventX() + i.left);
    data.setY(event.getEventY() + i.top);
    if (drag == -1)
      {
        data.setId(MouseEvent.MOUSE_MOVED);
      }
    else
      {
        data.setId(MouseEvent.MOUSE_DRAGGED);
      }
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

        ComponentEvent ce;
        if (resized) {
            EventData d = new EventData();
            d.setSource(cacioComponent);
            d.setId(ComponentEvent.COMPONENT_RESIZED);
            waitingEvents.add(d);
        }
        if (moved) {
            EventData d = new EventData();
            d.setSource(cacioComponent);
            d.setId(ComponentEvent.COMPONENT_MOVED);
            waitingEvents.add(d);
        }

        Rectangle r = awtComponent.getBounds();
        EventData d = new EventData();
        d.setSource(awtComponent);
        d.setId(PaintEvent.UPDATE);
        d.setUpdateRect(r);
        waitingEvents.add(d);
    }

  }
  
  private void handleConfigureNotify(ConfigureNotify event)
  {
    Integer key = new Integer(event.window_id);
    CacioComponent cacioComponent = windows.get(key);

    if (EscherToolkit.DEBUG)
      System.err.println("resize request for window id: " + key);

    // Detect and report size changes.
    clearWindow(cacioComponent, event.x(), event.y(), event.width(), event.height());

    if (waitingEvents.size() > 0) {
        data = waitingEvents.removeFirst();
    }
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

    data.setSource(awtComponent);
    data.setId(PaintEvent.UPDATE);
    data.setUpdateRect(r);
  }
    
  private void handleDestroyNotify(DestroyNotify destroyNotify)
  {
    if (EscherToolkit.DEBUG)
      System.err.println("DestroyNotify event: " + destroyNotify);
    
    Integer key = new Integer(destroyNotify.event_window_id);
    CacioComponent cacioComponent = windows.get(key);
    Component awtComp = cacioComponent.getAWTComponent();
    if (awtComp instanceof Window) {
        data.setSource(awtComp);
        data.setId(WindowEvent.WINDOW_CLOSED);
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
            data.setSource(awtComp);
            data.setId(WindowEvent.WINDOW_CLOSING);
        }
      }
  }
    
  private void handleEvent(Event xEvent)
  {
    data.clear();

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
          data.setSource(awtComponent);
          data.setId(WindowEvent.WINDOW_STATE_CHANGED);
      }
      break;
    case ConfigureNotify.CODE:
      this.handleConfigureNotify((ConfigureNotify) xEvent);
      break;
    case FocusIn.CODE:
        this.handleFocusEvent((FocusIn) xEvent);
        break;
    default:
      if (EscherToolkit.DEBUG)
        System.err.println("Unhandled X event: " + xEvent);
    }
  }

  private void handleFocusEvent(FocusIn focusEvent) {
      Integer key = new Integer(focusEvent.getEventWindowID());
      CacioComponent cacioComponent = windows.get(key);
      Component awtComponent = cacioComponent.getAWTComponent();
      java.awt.event.FocusEvent fe =
          new java.awt.event.FocusEvent(awtComponent,
                                       java.awt.event.FocusEvent.FOCUS_GAINED);
      cacioComponent.handlePeerEvent(fe);
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
        data.setSource(awtComponent);
        data.setId(KeyEvent.KEY_PRESSED);
        data.setTime(when);
        data.setModifiers(awtMods);
        data.setKeyCode(keyCode);
        data.setKeyChar(KeyEvent.CHAR_UNDEFINED);
        if (keyChar != KeyEvent.CHAR_UNDEFINED)
          {
            EventData d = new EventData();
            d.setSource(awtComponent);
            d.setId(KeyEvent.KEY_TYPED);
            d.setTime(when);
            d.setModifiers(awtMods);
            d.setKeyCode(KeyEvent.VK_UNDEFINED);
            d.setKeyChar(keyChar);
            waitingEvents.add(d);
          }
          
      }
    else
      {
        data.setSource(awtComponent);
        data.setId(KeyEvent.KEY_RELEASED);
        data.setTime(when);
        data.setModifiers(awtMods);
        data.setKeyCode(keyCode);
        data.setKeyChar(KeyEvent.CHAR_UNDEFINED);
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
