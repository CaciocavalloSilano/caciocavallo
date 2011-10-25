package net.java.openjdk.cacio.ctc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.java.openjdk.cacio.ctc.junit.CacioFESTRunner;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CacioFESTRunner.class)
public class SimpleFESTTest {

    private JFrame frame;
    private FrameFixture ff;

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
    public void testButton() {
        JButtonFixture b = ff.button("button");
        b.requireText("TEST");
        b.click();
        b.requireText("FLUFF");
        b.click();
        b.requireText("TEST");
    }
 
    @Test
    @GUITest
    public void testButtonKeyActivate() {
        JButtonFixture b = ff.button("button");
        b.requireText("TEST");
        b.focus();
        b.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_SPACE));
        b.requireText("FLUFF");
        b.pressAndReleaseKey(KeyPressInfo.keyCode(KeyEvent.VK_SPACE));
        b.requireText("TEST");
    }
}
