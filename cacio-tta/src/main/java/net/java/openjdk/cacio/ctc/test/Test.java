package net.java.openjdk.cacio.ctc.test;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Test {

    public static void main(String[] args) throws Exception {
        JFrame f = new JFrame();
        f.setContentPane(new JLabel("Hello World"));
        f.setSize(200, 100);
        f.setVisible(true);
        Thread.sleep(1000);
        Robot r = new Robot();
        BufferedImage capture = r.createScreenCapture(new Rectangle(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,  Toolkit.getDefaultToolkit().getScreenSize().height));
        ImageIO.write(capture, "png", new File("test.png"));
        f.dispose();
    }
}
