package sun.awt.peer.test;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Panel;

public class TestList extends Panel {

    TestList()  {
        setLayout(new GridLayout(1, 1));
        List l = new List();
        l.add("This");
        l.add("is");
        l.add("a");
        l.add("cool");
        l.add("List");
        add(l);
    }
    public static void main(String[] args) {
        Frame f = new Frame();
        Panel testPanel = new TestList();
        f.add(testPanel);
        f.setSize(400, 300);
        f.setVisible(true);
    }

}
