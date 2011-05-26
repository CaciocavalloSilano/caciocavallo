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
	screen.addEvent(data);
	data.setTime(System.currentTimeMillis());
	data.setSource(screen);

	System.out.println("Keyboard: "+getCharForKeyCode(keySym, shift));
	
	if (down) {
	    data.setId(KeyEvent.KEY_PRESSED);
	    data.setKeyCode(getJavaKeycodeForKeySym(keySym, shift));
	    generateTypedEvent(data, keySym, keyChar, shift);
	} else {
	    data.setId(KeyEvent.KEY_RELEASED);
	    data.setKeyCode(getJavaKeycodeForKeySym(keySym, shift));
	}

	lastEvent = data;
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

	switch (keySym) {
	
	case 32: 
	    return ' ';
	case 59:
	    return ';';
	case 61:
	    return '=';
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

// switch ( evt.keyCode ) {
// case 8 : keysym = 0xFF08; break; // BACKSPACE
// case 9 : keysym = 0xFF09; break; // TAB
// case 13 : keysym = 0xFF0D; break; // ENTER
// case 27 : keysym = 0xFF1B; break; // ESCAPE
// case 45 : keysym = 0xFF63; break; // INSERT
// case 46 : keysym = 0xFFFF; break; // DELETE
// case 36 : keysym = 0xFF50; break; // HOME
// case 35 : keysym = 0xFF57; break; // END
// case 33 : keysym = 0xFF55; break; // PAGE_UP
// case 34 : keysym = 0xFF56; break; // PAGE_DOWN
// case 37 : keysym = 0xFF51; break; // LEFT
// case 38 : keysym = 0xFF52; break; // UP
// case 39 : keysym = 0xFF53; break; // RIGHT
// case 40 : keysym = 0xFF54; break; // DOWN
// case 112 : keysym = 0xFFBE; break; // F1
// case 113 : keysym = 0xFFBF; break; // F2
// case 114 : keysym = 0xFFC0; break; // F3
// case 115 : keysym = 0xFFC1; break; // F4
// case 116 : keysym = 0xFFC2; break; // F5
// case 117 : keysym = 0xFFC3; break; // F6
// case 118 : keysym = 0xFFC4; break; // F7
// case 119 : keysym = 0xFFC5; break; // F8
// case 120 : keysym = 0xFFC6; break; // F9
// case 121 : keysym = 0xFFC7; break; // F10
// case 122 : keysym = 0xFFC8; break; // F11
// case 123 : keysym = 0xFFC9; break; // F12
// case 16 : keysym = 0xFFE1; break; // SHIFT
// case 17 : keysym = 0xFFE3; break; // CONTROL
// //case 18 : keysym = 0xFFE7; break; // Left Meta (Mac Option)
// case 18 : keysym = 0xFFE9; break; // Left ALT (Mac Command)
// default : keysym = evt.keyCode; break;
// }
//
// /* Remap symbols */
// switch (keysym) {
// case 186 : keysym = 59; break; // ; (IE)
// case 187 : keysym = 61; break; // = (IE)
// case 188 : keysym = 44; break; // , (Mozilla, IE)
// case 109 : // - (Mozilla, Opera)
// if (Util.Engine.gecko || Util.Engine.presto) {
// keysym = 45; }
// break;
// case 189 : keysym = 45; break; // - (IE)
// case 190 : keysym = 46; break; // . (Mozilla, IE)
// case 191 : keysym = 47; break; // / (Mozilla, IE)
// case 192 : keysym = 96; break; // ` (Mozilla, IE)
// case 219 : keysym = 91; break; // [ (Mozilla, IE)
// case 220 : keysym = 92; break; // \ (Mozilla, IE)
// case 221 : keysym = 93; break; // ] (Mozilla, IE)
// case 222 : keysym = 39; break; // ' (Mozilla, IE)
// }
//
// /* Remap shifted and unshifted keys */
// if (!!evt.shiftKey) {
// switch (keysym) {
// case 48 : keysym = 41 ; break; // ) (shifted 0)
// case 49 : keysym = 33 ; break; // ! (shifted 1)
// case 50 : keysym = 64 ; break; // @ (shifted 2)
// case 51 : keysym = 35 ; break; // # (shifted 3)
// case 52 : keysym = 36 ; break; // $ (shifted 4)
// case 53 : keysym = 37 ; break; // % (shifted 5)
// case 54 : keysym = 94 ; break; // ^ (shifted 6)
// case 55 : keysym = 38 ; break; // & (shifted 7)
// case 56 : keysym = 42 ; break; // * (shifted 8)
// case 57 : keysym = 40 ; break; // ( (shifted 9)
//
// case 59 : keysym = 58 ; break; // : (shifted `)
// case 61 : keysym = 43 ; break; // + (shifted ;)
// case 44 : keysym = 60 ; break; // < (shifted ,)
// case 45 : keysym = 95 ; break; // _ (shifted -)
// case 46 : keysym = 62 ; break; // > (shifted .)
// case 47 : keysym = 63 ; break; // ? (shifted /)
// case 96 : keysym = 126; break; // ~ (shifted `)
// case 91 : keysym = 123; break; // { (shifted [)
// case 92 : keysym = 124; break; // | (shifted \)
// case 93 : keysym = 125; break; // } (shifted ])
// case 39 : keysym = 34 ; break; // " (shifted ')
// }
// } else if ((keysym >= 65) && (keysym <=90)) {
// /* Remap unshifted A-Z */
// keysym += 32;
// } else if (evt.keyLocation === 3) {
// // numpad keys
// switch (keysym) {
// case 96 : keysym = 48; break; // 0
// case 97 : keysym = 49; break; // 1
// case 98 : keysym = 50; break; // 2
// case 99 : keysym = 51; break; // 3
// case 100: keysym = 52; break; // 4
// case 101: keysym = 53; break; // 5
// case 102: keysym = 54; break; // 6
// case 103: keysym = 55; break; // 7
// case 104: keysym = 56; break; // 8
// case 105: keysym = 57; break; // 9
// case 109: keysym = 45; break; // -
// case 110: keysym = 46; break; // .
// case 111: keysym = 47; break; // /
// }
// }

