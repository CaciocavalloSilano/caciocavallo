
package net.java.openjdk.cacio.ctc;

import static org.junit.Assert.assertEquals;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import net.java.openjdk.cacio.ctc.junit.CacioTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(value = CacioTestRunner.class)
public class DragMouseTest {

    @Test
    public void testDrag() throws AWTException, InvocationTargetException, InterruptedException {
        JFrame frame = new JFrame();
        frame.setSize(100, 100);
        frame.setVisible(true);

        Robot robot = new Robot();
        Point loc = frame.getContentPane().getLocationOnScreen();
        final List<MouseEvent> evts = new ArrayList<>();
        MouseAdapter l = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                evts.add(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                evts.add(e);
            }
        };
        frame.getContentPane().addMouseListener(l);
        frame.getContentPane().addMouseMotionListener(l);

        robot.mouseMove(loc.x + 20, loc.y + 20);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseMove(loc.x + 30, loc.y + 30);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        EventQueue.invokeAndWait(new Runnable() {
            
            @Override
            public void run() {
                // Only here for waiting for idle EQ.
            }
        });
        assertEquals(MouseEvent.MOUSE_MOVED, evts.get(0).getID());
        assertEquals(20, evts.get(0).getX());
        assertEquals(20, evts.get(0).getY());
        assertEquals(MouseEvent.MOUSE_DRAGGED, evts.get(1).getID());
        assertEquals(30, evts.get(1).getX());
        assertEquals(30, evts.get(1).getY());
    }
}
