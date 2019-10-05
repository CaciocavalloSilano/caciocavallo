/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.github.caciocavallosilano.cacio.ctc;

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
