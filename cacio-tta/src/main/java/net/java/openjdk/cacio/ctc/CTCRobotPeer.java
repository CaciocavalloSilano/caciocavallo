package net.java.openjdk.cacio.ctc;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.RobotPeer;

import sun.awt.peer.cacio.CacioMouseInfoPeer;
import sun.awt.peer.cacio.managed.EventData;

public class CTCRobotPeer implements RobotPeer {

    /**
     * 
     */
    private static final int BUTTON_MASK_CONVERSION_SHIFT = 6;

    /**
     * 
     */
    private static final int BUTTON_MASKS = InputEvent.BUTTON1_MASK | InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK;

    /**
     * 
     */
    private static final int BUTTON_DOWN_MASKS = InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK;

    private int currentModifiers = 0;
    
    private int currentX = 0;
    private int currentY = 0;

    private MouseClickSupport mouseClickSupport = new MouseClickSupport();

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
        ev.setClickCount(mouseClickSupport.getClickCount());
        return ev;
    }

    @Override
    public void mouseMove(int x, int y) {
        currentX = x;
        currentY = y;
        int id = isButtonPressed() ? MouseEvent.MOUSE_MOVED : MouseEvent.MOUSE_DRAGGED;
        EventData ev = mouseEvent(id, MouseEvent.NOBUTTON, false);
        CTCEventSource.getInstance().postEvent(ev);
        CacioMouseInfoPeer.getInstance().setMouseScreenCoordinates(x, y);
    }

    private boolean isButtonPressed() {
        return (currentModifiers & BUTTON_MASKS) == 0;
    }

    @Override
    public void mousePress(int buttons) {
        int buttonMask = buttonDownToButtonMask(buttons);
        int buttonDownMask = buttonToButtonDownMask(buttons);

        if (buttonDownMask != 0 && buttonMask != 0) {
            currentModifiers |= buttonMask;
            EventData ev = mouseEvent(MouseEvent.MOUSE_PRESSED, buttonDownMask, false);
            mouseClickSupport.mouseEvent(ev);
            ev = mouseEvent(MouseEvent.MOUSE_PRESSED, buttonDownMask, false);
            CTCEventSource.getInstance().postEvent(ev);
        }
    }

    @Override
    public void mouseRelease(int buttons) {
        int buttonMask = buttonDownToButtonMask(buttons);
        int buttonDownMask = buttonToButtonDownMask(buttons);

        if (buttonDownMask != 0 && buttonMask != 0) {
            EventData ev = mouseEvent(MouseEvent.MOUSE_RELEASED, buttonDownMask, (buttons & InputEvent.BUTTON3_MASK) != 0);
            CTCEventSource.getInstance().postEvent(ev);
            ev = mouseEvent(MouseEvent.MOUSE_RELEASED, buttonDownMask, false);
            mouseClickSupport.mouseEvent(ev);
            currentModifiers &= ~buttonMask;
        }
    }

    private int buttonToButtonDownMask(int buttons) {
        int buttonDownMask = buttons & BUTTON_DOWN_MASKS;
        buttonDownMask |= (buttons & BUTTON_MASKS) << BUTTON_MASK_CONVERSION_SHIFT;
        return buttonDownMask;
    }

    private int buttonDownToButtonMask(int buttons) {
        int buttonMask = buttons & BUTTON_MASKS;
        buttonMask |= (buttons & BUTTON_DOWN_MASKS) >> BUTTON_MASK_CONVERSION_SHIFT;
        return buttonMask;
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
