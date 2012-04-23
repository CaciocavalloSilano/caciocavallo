/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
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

package net.java.openjdk.cacio.ctc;

import java.awt.event.MouseEvent;

import sun.awt.peer.cacio.managed.EventData;

class MouseClickSupport {

    private static final long MULTI_CLICK_THRESHOLD = 400;

    private EventData lastPress;
    private int clickCount;

    void mouseEvent(EventData ev) {
        switch (ev.getId()) {
        case MouseEvent.MOUSE_PRESSED:
            handlePress(ev);
            break;
        case MouseEvent.MOUSE_RELEASED:
            handleRelease(ev);
            break;
        default:
            // Nothing to do.
        }
        
    }

    private void handlePress(EventData ev) {
        if (isInMultiClickThreshold(ev) && isClick(ev)) {
            clickCount++;
        } else {
            clickCount = 1;
        }
        lastPress = ev;
    }

    private boolean isInMultiClickThreshold(EventData ev) {
        return lastPress  != null && ev.getTime() - lastPress.getTime() < MULTI_CLICK_THRESHOLD;
    }

    private void handleRelease(EventData ev) {
        if (isClick(ev)) {
            generateClick(ev);
        }
    }

    private void generateClick(EventData ev) {
        
        ev.setId(MouseEvent.MOUSE_CLICKED);
        ev.setSource(CTCScreen.getInstance());
        ev.setTime(System.currentTimeMillis());
        ev.setClickCount(clickCount);
        ev.setPopup(false);
        CTCEventSource.getInstance().postEvent(ev);
    }

    private boolean isClick(EventData ev) {
        return lastPress.getButton() == ev.getButton()
               && lastPress.getX() == ev.getX()
               && lastPress.getY() == ev.getY();
    }

    int getClickCount() {
        return clickCount;
    }
}
