package net.java.openjdk.cacio.ctc;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import junit.framework.Assert;

import net.java.openjdk.cacio.ctc.junit.CacioFESTRunner;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CacioFESTRunner.class)
//@RunWith(GUITestRunner.class)
public class SimpleFESTTest {

  private JFrame frame;
  private FrameFixture ff;

  @Before
  public void setUp() {
      GuiActionRunner.execute(new GuiTask() {
          
          @Override
          protected void executeInEDT() throws Throwable {
              frame = new JFrame();
              frame.setLayout(new FlowLayout());
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
              JTextField text = new JTextField("Hallo");
              text.setName("text");
              text.setText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()`~-_=+[{]}\\|;:'\",<.>/?");
              frame.add(text);
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

  @Test
  @GUITest
  public void testFocus() {
      JButtonFixture b = ff.button("button");
      b.focus();
      b.requireFocused();
      JTextComponentFixture t = ff.textBox("text");
      t.focus();
      t.requireFocused();
  }

  @Test
  @GUITest
  public void testEnterTextDE() {
      Locale.setDefault(Locale.GERMANY);
      JTextComponentFixture t = ff.textBox("text");
      t.deleteText();
//    t.enterText("\u00b3");
//    t.requireText("\u00b3");
      t.enterText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()`~-_=+[{]}\\|;:'\",<.>/?\u00b2\u00a7\u00b0\u20ac\u00b4\u00b5");
      t.requireText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()`~-_=+[{]}\\|;:'\",<.>/?\u00b2\u00a7\u00b0\u20ac\u00b4\u00b5");
  }

  @Test
  @GUITest
  public void testEnterTextEN() {
      Locale.setDefault(Locale.UK);
      JTextComponentFixture t = ff.textBox("text");
      t.deleteText();
      t.enterText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()`~-_=+[{]}\\|;:'\",<.>/?");
      t.requireText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()`~-_=+[{]}\\|;:'\",<.>/?");
  }
}
