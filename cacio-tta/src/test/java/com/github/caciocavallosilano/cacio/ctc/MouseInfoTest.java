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

package com.github.caciocavallosilano.cacio.ctc;

import static org.junit.Assert.assertEquals;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import com.github.caciocavallosilano.cacio.ctc.junit.CacioAssertJRunner;

import org.junit.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.runner.RunWith;

@RunWith(CacioAssertJRunner.class)
//@RunWith(GUITestRunner.class)
@DisabledOnOs(OS.LINUX)
public class MouseInfoTest {

    @Test
    @GUITest
    public void testSimpleMousePosition() throws AWTException {
        JFrame f = new JFrame();
        f.setSize(100, 100);
        f.setLocation(100, 100);
        f.setVisible(true);
        Robot r = new Robot();
        r.mouseMove(150, 150);
        Point p = f.getMousePosition();
        assertEquals(50, p.x);
        assertEquals(50, p.y);
    }

}
