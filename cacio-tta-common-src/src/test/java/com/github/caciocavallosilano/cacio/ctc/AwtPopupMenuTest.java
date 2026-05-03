/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the GNU General Public License version 2 with the
 * Classpath exception.
 */
package com.github.caciocavallosilano.cacio.ctc;

import com.github.caciocavallosilano.cacio.ctc.junit.CacioTest;
import org.assertj.swing.annotation.GUITest;
import org.junit.jupiter.api.Test;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reproduces issue #17: java.awt.PopupMenu does not hide when clicking
 * elsewhere on the screen, although JPopupMenu does.
 */
@CacioTest
public class AwtPopupMenuTest {

    @Test
    @GUITest
    public void awtPopupMenuHidesOnOutsideClick() throws Exception {
        JFrame f = new JFrame();
        f.setSize(300, 300);
        f.setLocation(100, 100);
        JPanel panel = new JPanel();
        f.setContentPane(panel);
        f.setVisible(true);

        PopupMenu popup = new PopupMenu();
        popup.add(new MenuItem("Item 1"));
        panel.add(popup);
        popup.show(panel, 20, 20);
        Thread.sleep(200);

        // Click outside the popup, well clear of any items.
        Robot r = new Robot(f.getGraphicsConfiguration().getDevice());
        Point clickAt = new Point(f.getX() + 250, f.getY() + 250);
        r.mouseMove(clickAt.x, clickAt.y);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        r.waitForIdle();
        Thread.sleep(200);

        // After an outside click the popup should not still be on screen.
        // We probe via the underlying JPopupMenu the peer delegates to:
        // a still-visible window means the bug is reproduced.
        boolean anyPopupVisible = false;
        for (Frame frame : Frame.getFrames()) {
            for (java.awt.Window w : frame.getOwnedWindows()) {
                if (w.isVisible() && w.getClass().getName().contains("Popup")) {
                    anyPopupVisible = true;
                }
            }
        }
        assertFalse(anyPopupVisible, "AWT PopupMenu should be dismissed by an outside click");

        f.dispose();
    }
}
