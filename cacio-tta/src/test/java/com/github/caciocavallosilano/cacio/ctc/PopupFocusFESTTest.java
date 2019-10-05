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

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiTask;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.timing.Pause;
import com.github.caciocavallosilano.cacio.ctc.junit.CacioAssertJRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.*;
import java.awt.*;

@RunWith(CacioAssertJRunner.class)
//@RunWith(GUITestRunner.class)
public class PopupFocusFESTTest {

    private JFrame frame;
    private FrameFixture ff;

    @Before
    public void setUp() throws Exception {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                frame = new JFrame();
                frame.setLayout(new GridLayout(2, 1));

                final JTextField textField = new JTextField(20);
                textField.setName("foo");
                frame.add(textField);

                JButton button = new JButton("button");
                button.setFocusable(false);
                button.setName("button");
                button.setToolTipText("tooltip");
                frame.add(button);

                frame.pack();
                frame.setVisible(true);
            }

        });
        ff = new FrameFixture(frame);
    }

    @After
    public void tearDown() throws Exception {
        ff.cleanUp();
        GuiActionRunner.execute(new GuiTask() {

            @Override
            protected void executeInEDT() throws Throwable {
                frame.dispose();
                frame = null;
            }
        });
    }

    @Test
    @GUITest
    public void testFocusWithPopup() {
        JButtonFixture buttonFixture =  ff.button("button");

        // move the mouse over the button and wait for a tooltip
        ff.robot().moveMouse(buttonFixture.target());
        Pause.pause(1500);

        buttonFixture.click();

        JTextComponentFixture foo = ff.textBox("foo");
        foo.enterText("test");
    }
}
