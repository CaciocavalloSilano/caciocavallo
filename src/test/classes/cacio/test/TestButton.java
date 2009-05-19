package cacio.test;

import java.awt.*;
public class TestButton {

    public static void main(String[] args) {
        Frame f = new Frame();
        f.add(new Button("Hello World"));
        f.setSize(300, 200);
        f.setVisible(true);
    }
}