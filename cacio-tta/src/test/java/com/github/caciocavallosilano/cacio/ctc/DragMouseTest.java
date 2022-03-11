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

package com.github.caciocavallosilano.cacio.ctc;

import static org.junit.Assert.assertEquals;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import com.github.caciocavallosilano.cacio.ctc.junit.CacioTestRunner;

import org.assertj.swing.timing.Pause;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(value = CacioTestRunner.class)
public class DragMouseTest {

    @Test
    public void testDrag() throws AWTException, InvocationTargetException, InterruptedException {
        JFrame frame = new JFrame();
        frame.setSize(100, 100);
        frame.setVisible(true);

        Robot robot = new Robot();
        Point loc = frame.getContentPane().getLocationOnScreen();
        final List<MouseEvent> evts = new ArrayList<>();
        MouseAdapter l = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                evts.add(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                evts.add(e);
            }
        };
        frame.getContentPane().addMouseListener(l);
        frame.getContentPane().addMouseMotionListener(l);

        robot.mouseMove(loc.x + 20, loc.y + 20);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseMove(loc.x + 30, loc.y + 30);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        EventQueue.invokeAndWait(new Runnable() {
            
            @Override
            public void run() {
                // Only here for waiting for idle EQ.
            }
        });
        Pause.pause(100);
        assertEquals(MouseEvent.MOUSE_MOVED, evts.get(0).getID());
        assertEquals(20, evts.get(0).getX());
        assertEquals(20, evts.get(0).getY());
        assertEquals(MouseEvent.MOUSE_DRAGGED, evts.get(1).getID());
        assertEquals(30, evts.get(1).getX());
        assertEquals(30, evts.get(1).getY());
    }
}
