package cacio.test;


import java.awt.*;

public class TestTextField {

  public static void main(String[] args) {

      Frame f = new Frame();
      f.add(new TextField("Hello World"));
      f.setSize(300, 50);
      f.setVisible(true);
  }
}
