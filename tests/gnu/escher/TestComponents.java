package gnu.escher;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;

public class TestComponents extends Canvas {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Frame f = new Frame();
        f.setLayout(new GridLayout(2, 1));
        f.add(new TestComponents());
        f.add(new Label("Hello World"));
        f.add(new Button("Hello button"));
        f.setSize(400, 600);
        f.setVisible(true);
    }

    public void paint(Graphics g) {
        System.out.println("Painting the canvas thingy...");
        g.setColor(Color.RED);
        System.out.println("canvas width: " + getWidth());
        if (getWidth() < 0) {
            Thread.dumpStack();
            System.exit(0);
        }
        System.out.println("canvas height: " + getHeight());
        g.drawLine(0, 0, getWidth(), getHeight());
        g.drawLine(0, getHeight(), getWidth(), 0);
    }
}
