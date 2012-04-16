package net.java.openjdk.cacio.ctc;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.RobotPeer;

import sun.awt.peer.cacio.managed.EventData;

public class CTCRobotPeer implements RobotPeer {

    private int currentModifiers = 0;
    
    private int currentX = 0;
    private int currentY = 0;

    private EventData mouseEvent(int id, int currentButton, boolean popup) {
        EventData ev = new EventData();
        ev.setId(id);
        ev.setSource(CTCScreen.getInstance());
        ev.setTime(System.currentTimeMillis());
        ev.setModifiers(currentModifiers);
        ev.setX(currentX);
        ev.setY(currentY);
        ev.setButton(currentButton);
        ev.setPopup(popup);
        return ev;
    }

    @Override
    public void mouseMove(int x, int y) {
        currentX = x;
        currentY = y;
        EventData ev = mouseEvent(MouseEvent.MOUSE_MOVED, MouseEvent.NOBUTTON, false);
        CTCEventSource.getInstance().postEvent(ev);
    }

    @Override
    public void mousePress(int buttons) {
        if ((buttons & InputEvent.BUTTON1_DOWN_MASK) != 0 || (buttons & InputEvent.BUTTON1_MASK) != 0) {
            currentModifiers |= InputEvent.BUTTON1_MASK;
            EventData ev = mouseEvent(MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON1_DOWN_MASK, false);
            CTCEventSource.getInstance().postEvent(ev);
        }
        if ((buttons & InputEvent.BUTTON2_DOWN_MASK) != 0 || (buttons & InputEvent.BUTTON2_MASK) != 0) {
            currentModifiers |= InputEvent.BUTTON2_MASK;
            EventData ev = mouseEvent(MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON2_DOWN_MASK, false);
            CTCEventSource.getInstance().postEvent(ev);
        }
        if ((buttons & InputEvent.BUTTON3_DOWN_MASK) != 0 || (buttons & InputEvent.BUTTON3_MASK) != 0) {
            currentModifiers |= InputEvent.BUTTON3_MASK;
            EventData ev = mouseEvent(MouseEvent.MOUSE_PRESSED, MouseEvent.BUTTON3_DOWN_MASK, true);
            CTCEventSource.getInstance().postEvent(ev);
        }
    }

    @Override
    public void mouseRelease(int buttons) {
        if ((buttons & InputEvent.BUTTON3_DOWN_MASK) != 0 || (buttons & InputEvent.BUTTON3_MASK) != 0) {
            EventData ev = mouseEvent(MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON3_DOWN_MASK, true);
            CTCEventSource.getInstance().postEvent(ev);
            currentModifiers &= ~InputEvent.BUTTON3_MASK;
        }
        if ((buttons & InputEvent.BUTTON2_DOWN_MASK) != 0 || (buttons & InputEvent.BUTTON2_MASK) != 0) {
            EventData ev = mouseEvent(MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON2_DOWN_MASK, false);
            CTCEventSource.getInstance().postEvent(ev);
            currentModifiers &= ~InputEvent.BUTTON2_MASK;
        }
        if ((buttons & InputEvent.BUTTON1_DOWN_MASK) != 0 || (buttons & InputEvent.BUTTON1_MASK) != 0) {
            EventData ev = mouseEvent(MouseEvent.MOUSE_RELEASED, MouseEvent.BUTTON1_DOWN_MASK, false);
            CTCEventSource.getInstance().postEvent(ev);
            currentModifiers &= ~InputEvent.BUTTON1_MASK;
        }
    }

    @Override
    public void mouseWheel(int wheelAmt) {
        // TODO Auto-generated method stub

    }

    private void keyEvent(int keycode, int id) {
        EventData ev = new EventData();
        ev.setSource(CTCScreen.getInstance());
        ev.setId(id);
        ev.setTime(System.currentTimeMillis());
        ev.setModifiers(currentModifiers);
        ev.setKeyCode(keycode);
        CTCEventSource.getInstance().postEvent(ev);
    }

    @Override
    public void keyPress(int keycode) {
        if (keycode == KeyEvent.VK_SHIFT) {
          currentModifiers |= KeyEvent.SHIFT_MASK;
        }
        if (keycode == KeyEvent.VK_CONTROL) {
          currentModifiers |= KeyEvent.CTRL_MASK;
        }
        if (keycode == KeyEvent.VK_ALT_GRAPH) {
            currentModifiers |= KeyEvent.ALT_GRAPH_MASK;
        }
        keyEvent(keycode, KeyEvent.KEY_PRESSED);
        
        char keychar = getKeyCharFromCodeAndMods(keycode, currentModifiers);
        if (keychar != KeyEvent.CHAR_UNDEFINED) {
          EventData ev = new EventData();
          ev.setSource(CTCScreen.getInstance());
          ev.setId(KeyEvent.KEY_TYPED);
          ev.setTime(System.currentTimeMillis());
          ev.setModifiers(currentModifiers);
          ev.setKeyChar(keychar);
          CTCEventSource.getInstance().postEvent(ev);
        }
    }

    private char getKeyCharFromCodeAndMods(int keyCode, int modifiers) {
        return KeyStrokeMappingFactory.getInstance().getKeyStrokeMapping().getKeyChar(keyCode, modifiers);
    }

    @Override
    public void keyRelease(int keycode) {
      if (keycode == KeyEvent.VK_SHIFT) {
        currentModifiers &= ~KeyEvent.SHIFT_MASK;
      }
      if (keycode == KeyEvent.VK_CONTROL) {
        currentModifiers &= ~KeyEvent.CTRL_MASK;
      }
      if (keycode == KeyEvent.VK_ALT_GRAPH) {
          currentModifiers &= ~KeyEvent.ALT_GRAPH_MASK;
      }
        keyEvent(keycode, KeyEvent.KEY_RELEASED);
    }
 
    @Override
    public int getRGBPixel(int x, int y) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int[] getRGBPixels(Rectangle bounds) {
        return CTCScreen.getInstance().getRGBPixels(bounds);
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

}
