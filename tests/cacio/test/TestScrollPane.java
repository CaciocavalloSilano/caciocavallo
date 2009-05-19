package cacio.test;

import java.awt.*;

public class TestScrollPane {

  static class TestComponent extends Panel {
      public void paint(Graphics g) {
      g.setColor(Color.RED);
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(Color.BLACK);
      g.drawLine(0, 0, getWidth(), getHeight());
      g.drawLine(0, getHeight(), getWidth(), 0);
    }
  }

  public static void main(String[] args) {
    Frame f = new Frame();
    TestComponent c = new TestComponent();
    ScrollPane sp = new ScrollPane(ScrollPane.SCROLLBARS_ALWAYS);
    sp.add(c);
    f.add(sp);
    c.setPreferredSize(new Dimension(500, 500));
    f.setSize(200, 200);
    f.setVisible(true);
  }
}
