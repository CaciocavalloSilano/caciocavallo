package net.java.openjdk.awt.peer.web;

import java.awt.event.*;

import sun.awt.peer.cacio.managed.*;
import static java.awt.event.KeyEvent.*;

public class WebKeyboardStateTracker {
    WebScreen screen;

    EventData lastEvent = null;

    public WebKeyboardStateTracker(WebScreen screen) {
	this.screen = screen;
    }

    public void trackKeyEvent(boolean down, int keySym, char keyChar, boolean ctrl, boolean shift, boolean alt) {
	EventData data = new EventData();
	data.setTime(System.currentTimeMillis());
	data.setSource(screen);

//	System.out.println("Keyboard: "+getCharForKeyCode(keySym, shift));
	
	if (down) {
	    data.setId(KeyEvent.KEY_PRESSED);
	    data.setKeyCode(getJavaKeycodeForKeySym(keySym, shift));
	    generateTypedEvent(data, keySym, keyChar, shift);
	} else {
	    data.setId(KeyEvent.KEY_RELEASED);
	    data.setKeyCode(getJavaKeycodeForKeySym(keySym, shift));
	}

	lastEvent = data;
	screen.addEvent(data);
    }

    protected void generateTypedEvent(EventData pressedEvent, int keySym, char keyChar, boolean shift) {

	char selChar = getCharForKeyCode(keySym, shift);
	if (selChar != 0) {
	    EventData typedEvent = new EventData();
	    typedEvent.setId(KeyEvent.KEY_TYPED);
	    typedEvent.setTime(System.currentTimeMillis());
	    typedEvent.setSource(screen);
	    typedEvent.setKeyChar(getCharForKeyCode(keySym, shift));
//	    typedEvent.setKeyChar(s);
	    
	    screen.addEvent(typedEvent);
	}
    }

    protected char getCharForKeyCode(int keySym, boolean shift) {

	//System.out.println("KeySym: "+keySym);
	
	switch (keySym) {
	
	case 32: 
	    return ' ';
	case 33:
	    return '!';
	case 59:
	    return ';';
	case 61:
	    return '=';
	case 38:
	    return '&';
	case 40:
	    return '(';
	case 41:
	    return ')';
	case 44:
	    return ',';
	case 45:
	    return '-';
	case 46:
	    return '.';

	case 48:
	    return '0';
	case 49:
	    return '1';
	case 50:
	    return '2';
	case 51:
	    return '3';
	case 52:
	    return '4';
	case 53:
	    return '5';
	case 54:
	    return '6';
	case 55:
	    return '7';
	case 56:
	    return '8';
	case 57:
	    return '9';
	case 58: 
	    return ':';
	case 63:
	    return '?';
	case 92:
	    return 'ü';
	case 124:
	    return 'Ü';
	case 214:
	    return 'ö';
	case 196: 
	    return 'ä';
	}

	if ((keySym >= 97) && (keySym <= 122) || ((keySym >= 65) && (keySym <= 90))) {
	    return (char) keySym;
	}

	return 0;
    }

    protected int getJavaKeycodeForKeySym(int keySym, boolean shift) {

	switch (keySym) {
	case 0xFF08:
	    return VK_BACK_SPACE;
	case 0xFF09:
	    return VK_TAB;
	case 0xFF0D:
	    return VK_ENTER;
	case 0xFF1B:
	    return VK_ESCAPE;
	case 0xFF63:
	    return VK_INSERT;
	case 0xFFFF:
	    return VK_DELETE;
	case 0xFF50:
	    return VK_HOME;
	case 0xFF57:
	    return VK_END;
	case 0xFF51:
	    return VK_LEFT;
	case 0xFF52:
	    return VK_UP;
	case 0xFF53:
	    return VK_RIGHT;
	case 0xFF54:
	    return VK_DOWN;

	case 0xFFBE:
	    return VK_F1;
	case 0xFFBF:
	    return VK_F2;
	case 0xFFC0:
	    return VK_F3;
	case 0xFFC1:
	    return VK_F4;
	case 0xFFC2:
	    return VK_F5;
	case 0xFFC3:
	    return VK_F6;
	case 0xFFC4:
	    return VK_F7;
	case 0xFFC5:
	    return VK_F8;
	case 0xFFC6:
	    return VK_F9;
	case 0xFFC7:
	    return VK_F10;
	case 0xFFC8:
	    return VK_F11;
	case 0xFFC9:
	    return VK_F12;
	case 0xFFE1:
	    return VK_SHIFT;
	case 0xFFE3:
	    return VK_CONTROL;

	case 32:
	    return VK_SPACE;
	 
	case 59:
	    return VK_SEMICOLON;
	case 61:
	    return VK_EQUALS;
	case 44:
	    return VK_COMMA;
	case 45:
	    return VK_SEPARATOR;
	case 46:
	    return VK_COLON;
	    // TODO: Da fehlen noch einige!

	case 48:
	    return VK_0;
	case 49:
	    return VK_1;
	case 50:
	    return VK_2;
	case 51:
	    return VK_3;
	case 52:
	    return VK_4;
	case 53:
	    return VK_5;
	case 54:
	    return VK_6;
	case 55:
	    return VK_7;
	case 56:
	    return VK_8;
	case 57:
	    return VK_9;
	}

	if ((keySym >= 97) && (keySym <= 122) && !shift) {
	    return keySym - 32;
	} else if ((keySym >= 65) && (keySym <= 90) && shift) {
	    return keySym;
	}

	return -1;
    }
}