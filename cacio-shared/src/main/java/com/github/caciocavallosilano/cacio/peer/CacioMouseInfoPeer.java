/*
 * Copyright 2012 Red Hat, Inc.
 *
 * This file is part of Thermostat.
 *
 * Thermostat is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2, or (at your
 * option) any later version.
 *
 * Thermostat is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thermostat; see the file COPYING.  If not see
 * <http://www.gnu.org/licenses/>.
 *
 * Linking this code with other modules is making a combined work
 * based on this code.  Thus, the terms and conditions of the GNU
 * General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this code give
 * you permission to link this code with independent modules to
 * produce an executable, regardless of the license terms of these
 * independent modules, and to copy and distribute the resulting
 * executable under terms of your choice, provided that you also
 * meet, for each linked independent module, the terms and conditions
 * of the license of that module.  An independent module is a module
 * which is not derived from or based on this code.  If you modify
 * this code, you may extend this exception to your version of the
 * library, but you are not obligated to do so.  If you do not wish
 * to do so, delete this exception statement from your version.
 */

package com.github.caciocavallosilano.cacio.peer;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.peer.MouseInfoPeer;

public class CacioMouseInfoPeer implements MouseInfoPeer {

    private static CacioMouseInfoPeer instance = new CacioMouseInfoPeer();

    public static CacioMouseInfoPeer getInstance() {
        return instance;
    }

    private int x;
    private int y;

    @Override
    public int fillPointWithCoords(Point point) {
        point.x = x;
        point.y = y;
        // TODO: Provide more intelligent implementation for the screen device.
        // Returning 0 should be ok for most Cacio backends though.
        return 0;
    }

    public void setMouseScreenCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isWindowUnderMouse(Window w) {
        // TODO: This assumes we get the windows in the order in which
        // they are stacked. This is probably true for now, since
        // Cacio does not implement toFront(), toBack() or any other
        // Z-ordering of windows, but is otherwise a bad idea. Needs
        // to be fixed as soon as we implement Z-ordering of windows.
        Window[] windows = Window.getWindows();
        Window found = null;
        for (Window window : windows) {
            if (window.isVisible() && isMouseInWindowRegion(w)) {
                found = window;
                break;
            }

        }
        return found == w;
    }

    private boolean isMouseInWindowRegion(Window w) {
        Point l = w.getLocationOnScreen();
        Dimension size = w.getSize();
        return l.x <= x && x < (l.x + size.width) && l.y <= y && y < (l.y + size.height);
    }

}
