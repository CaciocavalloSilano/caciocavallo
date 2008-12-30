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

package sun.awt.peer.cacio;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextAreaPeer;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class CacioTextAreaPeer extends CacioComponentPeer implements TextAreaPeer {

    class SwingScrollPane extends JScrollPane implements CacioSwingComponent {

        private TextArea textArea;

        SwingScrollPane(SwingTextArea ta, TextArea awtTextArea) {
            super(ta);
            textArea = awtTextArea;
        }

        @Override
        public JComponent getJComponent() {
            return this;
        }

        @Override
        public void handleFocusEvent(FocusEvent ev) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void handleKeyEvent(KeyEvent ev) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void handleMouseEvent(MouseEvent ev) {
            ev.setSource(this);
            processMouseEvent(ev);
        }

        @Override
        public void handleMouseMotionEvent(MouseEvent ev) {
            ev.setSource(this);
            processMouseMotionEvent(ev);
        }
        
        @Override
        public Point getLocationOnScreen() {
            
            return CacioTextAreaPeer.this.getLocationOnScreen();
        }

        @Override
        public Image createImage(int w, int h) {
            
            return CacioTextAreaPeer.this.createImage(w, h);
        }
        
        @Override
        public Graphics getGraphics() {
            return CacioTextAreaPeer.this.getGraphics();
        }
        
        @Override
        public boolean isShowing() {
            boolean retVal = false;
            if (textArea != null)
                retVal = textArea.isShowing();
            return retVal;
        }

        @Override
        public Container getParent() {
            Container par = null;
            if (textArea != null)
                par = textArea.getParent();
            return par;
        }
    }

    class SwingTextArea extends JTextArea implements CacioSwingComponent {

        private TextArea textArea;

        SwingTextArea(TextArea ta) {
            textArea = ta;
        }

        @Override
        public JComponent getJComponent() {
            return this;
        }

        @Override
        public void handleFocusEvent(FocusEvent ev) {
            // TODO: Implement.
        }

        @Override
        public void handleKeyEvent(KeyEvent ev) {
            // TODO: Implement.
        }

        @Override
        public void handleMouseEvent(MouseEvent ev) {
            // TODO: Implement.
        }

        @Override
        public void handleMouseMotionEvent(MouseEvent ev) {
            // TODO: Implement.
        }
        
        @Override
        public Point getLocationOnScreen() {
            
            return CacioTextAreaPeer.this.getLocationOnScreen();
        }

        @Override
        public Image createImage(int w, int h) {
            
            return CacioTextAreaPeer.this.createImage(w, h);
        }
        
        @Override
        public Graphics getGraphics() {
            return CacioTextAreaPeer.this.getGraphics();
        }
        
        @Override
        public boolean isShowing() {
            boolean retVal = false;
            if (textArea != null)
                retVal = textArea.isShowing();
            return retVal;
        }

        @Override
        public Container getParent() {
            Container par = null;
            if (textArea != null)
                par = textArea.getParent();
            return par;
        }
    }

    private SwingTextArea textArea;

    public CacioTextAreaPeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }

    @Override
    void initSwingComponent() {
        TextArea awtTextArea = (TextArea) awtComponent;
        textArea = new SwingTextArea(awtTextArea);
        int sbv = awtTextArea.getScrollbarVisibility();
        if (sbv != TextArea.SCROLLBARS_NONE) {
            SwingScrollPane sp = new SwingScrollPane(textArea, awtTextArea);
            if (sbv == TextArea.SCROLLBARS_BOTH) {
                sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            } else if (sbv == TextArea.SCROLLBARS_HORIZONTAL_ONLY) {
                sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            } else if (sbv == TextArea.SCROLLBARS_VERTICAL_ONLY) {
                sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            }
            setSwingComponent(sp);
            sp.addNotify();
        } else {
            setSwingComponent(textArea);
        }
        setText(awtTextArea.getText());
        textArea.addNotify();
    }

    @Override
    public Dimension getMinimumSize(int rows, int columns) {
        return super.getMinimumSize();
    }

    @Override
    public Dimension getPreferredSize(int rows, int columns) {
        return super.getPreferredSize();
    }

    @Override
    public void insert(String text, int pos) {
        getTextArea().insert(text, pos);
    }

    @Override
    public void replaceRange(String text, int start, int end) {
        getTextArea().replaceRange(text, start, end);
    }

    @Override
    public int getCaretPosition() {
        return getTextArea().getCaretPosition();
    }

    @Override
    public InputMethodRequests getInputMethodRequests() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getSelectionEnd() {
        return getTextArea().getSelectionEnd();
    }

    @Override
    public int getSelectionStart() {
        return getTextArea().getSelectionStart();
    }

    @Override
    public String getText() {
        return getTextArea().getText();
    }

    @Override
    public void select(int selStart, int selEnd) {
        getTextArea().select(selStart, selEnd);
    }

    @Override
    public void setCaretPosition(int pos) {
        getTextArea().setCaretPosition(pos);
    }

    @Override
    public void setEditable(boolean editable) {
        getTextArea().setEditable(editable);
    }

    @Override
    public void setText(String text) {
        getTextArea().setText(text);
    }

    @Override
    public void layout() {

        getSwingComponent().getJComponent().doLayout();
//        getSwingComponent().getJComponent().invalidate();
//        getSwingComponent().getJComponent().validate();
    }

    private JTextArea getTextArea() {
        return textArea;
    }
}
