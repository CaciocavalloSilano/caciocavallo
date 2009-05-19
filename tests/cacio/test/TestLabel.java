package cacio.test;


import java.awt.*;
import java.awt.event.*;

public class TestLabel {
  public static void main(String[] args) {

    EventQueue eq1 = Toolkit.getDefaultToolkit().getSystemEventQueue();
    eq1.push(new EventQueue() {
        public void postEvent(AWTEvent ev) {
          super.postEvent(ev);
          //if (ev instanceof ActionEvent)
            System.err.println("posted event: " + ev);
        }
        public void dispatchEvent(AWTEvent ev) {
          super.dispatchEvent(ev);
          //if (ev instanceof ActionEvent)
          //System.err.println("dispatched event: " + ev);
        }
      });
    Frame f = new Frame();
    f.setLayout(new FlowLayout());
    Label l = new Label("Hello World");
    f.add(l);
    Button b = new Button("Button");
    b.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          System.err.println("button clicked");
        }
      });
    f.add(b);
    TextField tf = new TextField("Test TextField");
    f.add(tf);

    MenuBar menuBar = new MenuBar();
    Menu test = new Menu("test");
    test.add(new MenuItem("item1"));
    test.add(new MenuItem("item2"));
    test.add(new MenuItem("item3"));
    menuBar.add(test);
    f.setMenuBar(menuBar);
    f.pack();
    f.setVisible(true);
    System.err.println(f.getInsets());
  }
}
