/*
 * Copyright 2014 Entero Corporation. All Rights Reserved.
 * www.entero.com
 */
package net.java.openjdk.cacio.ctc;

import com.jidesoft.hints.AbstractIntelliHints;
import net.java.openjdk.cacio.ctc.junit.CacioFESTRunner;
import org.fest.swing.annotation.GUITest;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.v4_5.runner.GUITestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

//@RunWith(CacioFESTRunner.class)
@RunWith(GUITestRunner.class)
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
