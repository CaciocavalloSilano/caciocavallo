package net.java.openjdk.cacio.ctc;

import static java.awt.event.InputEvent.ALT_GRAPH_MASK;
import static java.awt.event.InputEvent.SHIFT_MASK;
import static java.awt.event.KeyEvent.VK_0;
import static java.awt.event.KeyEvent.VK_1;
import static java.awt.event.KeyEvent.VK_2;
import static java.awt.event.KeyEvent.VK_3;
import static java.awt.event.KeyEvent.VK_4;
import static java.awt.event.KeyEvent.VK_5;
import static java.awt.event.KeyEvent.VK_6;
import static java.awt.event.KeyEvent.VK_7;
import static java.awt.event.KeyEvent.VK_8;
import static java.awt.event.KeyEvent.VK_9;
import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_B;
import static java.awt.event.KeyEvent.VK_BACK_QUOTE;
import static java.awt.event.KeyEvent.VK_BACK_SLASH;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_CLOSE_BRACKET;
import static java.awt.event.KeyEvent.VK_COMMA;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_EQUALS;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_G;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_I;
import static java.awt.event.KeyEvent.VK_J;
import static java.awt.event.KeyEvent.VK_K;
import static java.awt.event.KeyEvent.VK_L;
import static java.awt.event.KeyEvent.VK_M;
import static java.awt.event.KeyEvent.VK_MINUS;
import static java.awt.event.KeyEvent.VK_N;
import static java.awt.event.KeyEvent.VK_O;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_PERIOD;
import static java.awt.event.KeyEvent.VK_Q;
import static java.awt.event.KeyEvent.VK_R;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SLASH;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_T;
import static java.awt.event.KeyEvent.VK_U;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_X;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;

import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;
import java.util.Map;

class KeyStrokeMappingDE extends AbstractKeyStrokeMapping implements KeyStrokeMapping {

    private Map<AWTKeyStroke, Character> map;

    KeyStrokeMappingDE() {
        map = getDefaultMap();

        map.put(keyStroke(VK_0, NO_MASK), '0');
        map.put(keyStroke(VK_0, SHIFT_MASK), '=');
        map.put(keyStroke(VK_0, ALT_GRAPH_MASK), '}');
        map.put(keyStroke(VK_1, NO_MASK), '1');
        map.put(keyStroke(VK_1, SHIFT_MASK), '!');
        map.put(keyStroke(VK_2, NO_MASK), '2');
        map.put(keyStroke(VK_2, SHIFT_MASK), '"');
        map.put(keyStroke(VK_2, ALT_GRAPH_MASK), '\u00b2');
        map.put(keyStroke(VK_3, NO_MASK), '3');
        map.put(keyStroke(VK_3, SHIFT_MASK), '\u00a7');
        map.put(keyStroke(VK_3, ALT_GRAPH_MASK), '\u00b3');
        map.put(keyStroke(VK_4, NO_MASK), '4');
        map.put(keyStroke(VK_4, SHIFT_MASK), '$');
        map.put(keyStroke(VK_5, NO_MASK), '5');
        map.put(keyStroke(VK_5, SHIFT_MASK), '%');
        map.put(keyStroke(VK_6, NO_MASK), '6');
        map.put(keyStroke(VK_6, SHIFT_MASK), '&');
        map.put(keyStroke(VK_7, NO_MASK), '7');
        map.put(keyStroke(VK_7, SHIFT_MASK), '/');
        map.put(keyStroke(VK_7, ALT_GRAPH_MASK), '{');
        map.put(keyStroke(VK_8, NO_MASK), '8');
        map.put(keyStroke(VK_8, SHIFT_MASK), '(');
        map.put(keyStroke(VK_8, ALT_GRAPH_MASK), '[');
        map.put(keyStroke(VK_9, NO_MASK), '9');
        map.put(keyStroke(VK_9, SHIFT_MASK), ')');
        map.put(keyStroke(VK_9, ALT_GRAPH_MASK), ']');
        map.put(keyStroke(VK_A, NO_MASK), 'a');
        map.put(keyStroke(VK_A, SHIFT_MASK), 'A');
        map.put(keyStroke(VK_B, NO_MASK), 'b');
        map.put(keyStroke(VK_B, SHIFT_MASK), 'B');
        map.put(keyStroke(VK_BACK_QUOTE, NO_MASK), '^');
        map.put(keyStroke(VK_BACK_QUOTE, SHIFT_MASK), '\u00b0');
        map.put(keyStroke(VK_BACK_SLASH, NO_MASK), '<');
        map.put(keyStroke(VK_BACK_SLASH, SHIFT_MASK), '>');
        map.put(keyStroke(VK_BACK_SLASH, ALT_GRAPH_MASK), '|');
        map.put(keyStroke(VK_C, NO_MASK), 'c');
        map.put(keyStroke(VK_C, SHIFT_MASK), 'C');
        map.put(keyStroke(VK_CLOSE_BRACKET, NO_MASK), '+');
        map.put(keyStroke(VK_CLOSE_BRACKET, SHIFT_MASK), '*');
        map.put(keyStroke(VK_CLOSE_BRACKET, ALT_GRAPH_MASK), '~');
        map.put(keyStroke(VK_COMMA, NO_MASK), ',');
        map.put(keyStroke(VK_COMMA, SHIFT_MASK), ';');
        map.put(keyStroke(VK_D, NO_MASK), 'd');
        map.put(keyStroke(VK_D, SHIFT_MASK), 'D');
        map.put(keyStroke(VK_DELETE, NO_MASK), '\u007f');
        map.put(keyStroke(VK_E, NO_MASK), 'e');
        map.put(keyStroke(VK_E, SHIFT_MASK), 'E');
        map.put(keyStroke(VK_E, ALT_GRAPH_MASK), '\u20ac');
        map.put(keyStroke(VK_EQUALS, NO_MASK), '\u00b4');
        map.put(keyStroke(VK_EQUALS, SHIFT_MASK), '`');
        map.put(keyStroke(VK_F, NO_MASK), 'f');
        map.put(keyStroke(VK_F, SHIFT_MASK), 'F');
        map.put(keyStroke(VK_G, NO_MASK), 'g');
        map.put(keyStroke(VK_G, SHIFT_MASK), 'G');
        map.put(keyStroke(VK_H, NO_MASK), 'h');
        map.put(keyStroke(VK_H, SHIFT_MASK), 'H');
        map.put(keyStroke(VK_I, NO_MASK), 'i');
        map.put(keyStroke(VK_I, SHIFT_MASK), 'I');
        map.put(keyStroke(VK_J, NO_MASK), 'j');
        map.put(keyStroke(VK_J, SHIFT_MASK), 'J');
        map.put(keyStroke(VK_K, NO_MASK), 'k');
        map.put(keyStroke(VK_K, SHIFT_MASK), 'K');
        map.put(keyStroke(VK_L, NO_MASK), 'l');
        map.put(keyStroke(VK_L, SHIFT_MASK), 'L');
        map.put(keyStroke(VK_M, NO_MASK), 'm');
        map.put(keyStroke(VK_M, SHIFT_MASK), 'M');
        map.put(keyStroke(VK_M, ALT_GRAPH_MASK), '\u00b5');
        map.put(keyStroke(VK_SLASH, SHIFT_MASK), '?');
        map.put(keyStroke(VK_SLASH, ALT_GRAPH_MASK), '\\');
        map.put(keyStroke(VK_N, NO_MASK), 'n');
        map.put(keyStroke(VK_N, SHIFT_MASK), 'N');
        map.put(keyStroke(VK_O, NO_MASK), 'o');
        map.put(keyStroke(VK_O, SHIFT_MASK), 'O');
        map.put(keyStroke(VK_P, NO_MASK), 'p');
        map.put(keyStroke(VK_P, SHIFT_MASK), 'P');
        map.put(keyStroke(VK_PERIOD, NO_MASK), '.');
        map.put(keyStroke(VK_PERIOD, SHIFT_MASK), ':');
        map.put(keyStroke(VK_Q, NO_MASK), 'q');
        map.put(keyStroke(VK_Q, SHIFT_MASK), 'Q');
        map.put(keyStroke(VK_Q, ALT_GRAPH_MASK), '@');
        map.put(keyStroke(VK_R, NO_MASK), 'r');
        map.put(keyStroke(VK_R, SHIFT_MASK), 'R');
        map.put(keyStroke(VK_S, NO_MASK), 's');
        map.put(keyStroke(VK_S, SHIFT_MASK), 'S');
        map.put(keyStroke(VK_MINUS, NO_MASK), '-');
        map.put(keyStroke(VK_MINUS, SHIFT_MASK), '_');
        map.put(keyStroke(VK_SPACE, NO_MASK), ' ');
        map.put(keyStroke(VK_T, NO_MASK), 't');
        map.put(keyStroke(VK_T, SHIFT_MASK), 'T');
        map.put(keyStroke(VK_U, NO_MASK), 'u');
        map.put(keyStroke(VK_U, SHIFT_MASK), 'U');
        map.put(keyStroke(VK_V, NO_MASK), 'v');
        map.put(keyStroke(VK_V, SHIFT_MASK), 'V');
        map.put(keyStroke(VK_W, NO_MASK), 'w');
        map.put(keyStroke(VK_W, SHIFT_MASK), 'W');
        map.put(keyStroke(VK_X, NO_MASK), 'x');
        map.put(keyStroke(VK_X, SHIFT_MASK), 'X');
        map.put(keyStroke(VK_Y, NO_MASK), 'y');
        map.put(keyStroke(VK_Y, SHIFT_MASK), 'Y');
        map.put(keyStroke(VK_Z, NO_MASK), 'z');
        map.put(keyStroke(VK_Z, SHIFT_MASK), 'Z');

    }

    @Override
    public char getKeyChar(int keyCode, int modifiers) {
        AWTKeyStroke stroke = keyStroke(keyCode, modifiers);
        Character ch = map.get(stroke);
        if (ch == null) {
            return KeyEvent.CHAR_UNDEFINED;
        } else {
            return ch.charValue();
        }
    }

    
}
