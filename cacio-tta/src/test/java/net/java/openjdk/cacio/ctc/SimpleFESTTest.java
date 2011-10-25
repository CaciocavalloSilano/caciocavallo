package net.java.openjdk.cacio.ctc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleFESTTest {

    private JFrame frame;

    @Before
    public void setUp() {
        GuiActionRunner.execute(new GuiTask() {
            
            @Override
            protected void executeInEDT() throws Throwable {
                frame = new JFrame();
                final JButton b = new JButton("TEST");
                b.addActionListener(new ActionListener() {
                    
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (b.getText().equals("TEST")) {
                            b.setText("FLUFF");
                        } else {
                            b.setText("TEST");
                        }
                    }
                });
                b.setName("button");
                frame.add(b);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    @After
    public void tearDown() throws Exception {
        GuiActionRunner.execute(new GuiTask() {
            
            @Override
            protected void executeInEDT() throws Throwable {
                frame.dispose();
                frame = null;
            }
        });
    }

    @Test
    public void testButton() {
        FrameFixture ff = new FrameFixture(frame);
        JButtonFixture b = ff.button("button");
        b.requireText("TEST");
        b.click();
        b.requireText("FLUFF");
        b.click();
        b.requireText("TEST");
        ff.cleanUp();
    }
 
    @Test
    public void testButtonKeyActivate() {
        FrameFixture ff = new FrameFixture(frame);
        JButtonFixture b = ff.button("button");
        b.requireText("TEST");
        b.focus();
        b.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_SPACE));
        b.requireText("FLUFF");
        b.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_SPACE));
        b.requireText("TEST");
        ff.cleanUp();
    }
}
