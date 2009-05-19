package cacio.test;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
public class TestTextArea {
  public static void main(String[] args) {
      /*
              try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
      */
    Frame f = new Frame();
    TextArea a = new TextArea();
    a.setText("Hello\tWorld\nThis is a cool AWT TextArea\nIt runs on OpenJDK7\nUsing Caciocavallo\nWith an X11 backend\nusing Swing's Nimbus L&F\nHello\tWorld\nThis is a cool AWT TextArea\nIt runs on OpenJDK7\nUsing Caciocavallo\nWith an X11 backend\nusing Swing's Nimbus L&F\nHello\tWorld\nThis is a cool AWT TextArea\nIt runs on OpenJDK7\nUsing Caciocavallo\nWith an X11 backend\nusing Swing's Nimbus L&F\nHello\tWorld\nThis is a cool AWT TextArea\nIt runs on OpenJDK7\nUsing Caciocavallo\nWith an X11 backend\nusing Swing's Nimbus L&F\nHello\tWorld\nThis is a cool AWT TextArea\nIt runs on OpenJDK7\nUsing Caciocavallo\nWith an X11 backend\nusing Swing's Nimbus L&F\nHello\tWorld\nThis is a cool AWT TextArea\nIt runs on OpenJDK7\nUsing Caciocavallo\nWith an X11 backend\nusing Swing's Nimbus L&F\n");
    f.add(a);
    f.setSize(200, 200);
    f.setVisible(true);
    while (true);
  }
}
