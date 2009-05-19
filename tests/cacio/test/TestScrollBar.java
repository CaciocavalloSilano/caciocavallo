package cacio.test;

import java.awt.*;

public class TestScrollBar {

    public static void main(String[] args) {
        Frame f = new Frame();
        f.setLayout(new FlowLayout());
        f.add(new Scrollbar(Scrollbar.VERTICAL));
        f.add(new Scrollbar(Scrollbar.HORIZONTAL));
        f.setSize(200, 100);
        f.setVisible(true);
    }
}