package net.java.openjdk.cacio.ctc;

import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_TAB;

import java.awt.AWTKeyStroke;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractKeyStrokeMapping implements KeyStrokeMapping {

    static final int NO_MASK = 0;

    Map<AWTKeyStroke,Character> getDefaultMap() {
        Map<AWTKeyStroke,Character> map = new HashMap<AWTKeyStroke,Character>();
        map.put(keyStroke(VK_BACK_SPACE, NO_MASK), '\b');
        map.put(keyStroke(VK_DELETE, NO_MASK), '\u007f');
        map.put(keyStroke(VK_ENTER, NO_MASK), '\n');
        if (isWindows()) map.put(keyStroke(VK_ENTER, NO_MASK), '\r');
        map.put(keyStroke(VK_ESCAPE, NO_MASK), '\u001b');
        map.put(keyStroke(VK_TAB, NO_MASK), '\t');
        return map;
    }

    AWTKeyStroke keyStroke(int keyCode, int modifiers) {
        return AWTKeyStroke.getAWTKeyStroke(keyCode, modifiers);
    }

    static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

}
