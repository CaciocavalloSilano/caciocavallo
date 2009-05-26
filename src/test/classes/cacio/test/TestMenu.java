/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package cacio.test;

import java.awt.CheckboxMenuItem;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class TestMenu {

    public static void main(String[] args) {
        Frame f = new Frame();
        MenuBar mb = new MenuBar();
        Menu m = new Menu("Menu");
        CheckboxMenuItem i = new CheckboxMenuItem("checkbox item");
        i.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                System.err.println("itemStateChanged: " + e);
            }

        });
        i.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.err.println("actionPerformed: " + e);
            }

        });
        m.add(i);
        mb.add(m);
        Menu m2 = new Menu("submenu");
        m2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.err.println("actionPerformed: " + e);
            }

        });
        m.add(m2);
        MenuItem i2 = new MenuItem("standard menu item");
        i2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.err.println("actionPerformed: " + e);
            }

        });
        m.add(i2);

        f.setMenuBar(mb);
        f.setSize(400, 300);
        f.setVisible(true);

        Menu helpMenu = new Menu("Help");
        mb.setHelpMenu(helpMenu);

        Menu m3 = new Menu("Menu2");
        mb.add(m3);

        Menu m4 = new Menu("Menu3");
        mb.add(m4);

        Menu m5 = new Menu("Menu4");
        mb.add(m5);

        Menu m6 = new Menu("Menu5");
        mb.add(m6);


    }
}
