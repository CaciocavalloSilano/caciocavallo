package cacio.test;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
public class TestList {

  public static void main(String[] args) {
    Frame f = new Frame();
    List l = new List();
    f.add(l);
    l.add("Item1");
    l.add("Item2");
    l.add("Item3");
    l.add("Item4");
    l.add("Item5");
    l.add("Item1");
    l.add("Item2");
    l.add("Item3");
    l.add("Item4");
    l.add("Item5");
    f.setSize(100, 100);
    f.setVisible(true);
    while (true);
  }
}
