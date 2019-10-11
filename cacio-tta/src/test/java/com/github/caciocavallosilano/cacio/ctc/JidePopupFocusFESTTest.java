/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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

import com.github.caciocavallosilano.cacio.ctc.junit.CacioAssertJRunner;
import com.jidesoft.hints.AbstractIntelliHints;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiTask;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

@RunWith(CacioAssertJRunner.class)
//@RunWith(GUITestRunner.class)
public class JidePopupFocusFESTTest {

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

                new AbstractIntelliHints(textField) {
                    protected JLabel label;

                    public JComponent createHintsComponent() {
                        JPanel panel1 = new JPanel(new BorderLayout());

                        label = new JLabel();
                        panel1.add(label, BorderLayout.BEFORE_FIRST_LINE);
                        return panel1;
                    }

                    @Override
                    protected KeyStroke[] getDelegateKeyStrokes() {
                        return new KeyStroke[0];
                    }

                    @Override
                    protected JComponent getDelegateComponent() {
                        return label;
                    }

                    @Override
                    public Object getSelectedHint() {
                        return null;
                    }

                    public boolean updateHints(Object value) {
                        label.setText(value.toString());
                        return true;
                    }
                };

                final JTextField textField2 = new JTextField(20);
                textField2.setName("bar");
                frame.add(textField2);

                frame.pack();
                frame.setSize(500, 500);
                frame.setLocation(0, 0);
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
        ff.textBox("foo").enterText("test");

        // trigger the popup
        ff.textBox("foo").pressKey(KeyEvent.VK_DOWN);

        ff.textBox("foo").enterText("test");

        ff.textBox("bar").enterText("bar");
    }

}
