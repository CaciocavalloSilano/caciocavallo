/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
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

package sun.awt.peer.test;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextArea;

public class TestTextArea extends Panel {

    TestTextArea() {
        setLayout(new GridLayout(1, 1));
        TextArea textArea1 = new TextArea("TextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\nTextArea with scrollbars\n", 50, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
        //textArea1.setText();
        add(textArea1);
    }

    public static void main(String[] args) {
        Frame f = new Frame();
        Panel testPanel = new TestTextArea();
        f.add(testPanel);
        f.setSize(400, 300);
        f.setVisible(true);
    }
}
