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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.LabelPeer;

import javax.swing.JComponent;
import javax.swing.JLabel;

class CacioLabelPeer extends CacioComponentPeer implements LabelPeer {

    public CacioLabelPeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
        // TODO Auto-generated constructor stub
    }

    /**
     * A spezialized Swing label used to paint the label for the AWT Label. 
     *
     * @author Roman Kennke (kennke@aicas.com)
     */
    private class SwingLabel extends JLabel implements CacioSwingComponent {

        Label label;

        SwingLabel(Label label) {
            this.label = label;
        }

        public void paint(Graphics g) {
            super.paint(g);
            System.out.println("CacioLabelPeer.paint: " + isShowing() + ", " + getBounds());
        }
        /**
         * Returns this label.
         * 
         * @return <code>this</code>
         */
        @Override
        public JComponent getJComponent() {
            return this;
        }

        /**
         * Handles mouse events by forwarding it to
         * <code>processMouseEvent()</code>.
         * 
         * @param ev the mouse event
         */
        @Override
        public void handleMouseEvent(MouseEvent ev) {
            processMouseEvent(ev);
        }

        /**
         * Handles mouse motion events by forwarding it to
         * <code>processMouseMotionEvent()</code>.
         * 
         * @param ev the mouse motion event
         */
        @Override
        public void handleMouseMotionEvent(MouseEvent ev) {
            processMouseMotionEvent(ev);
        }

        /**
         * Handles key events by forwarding it to <code>processKeyEvent()</code>.
         * 
         * @param ev the mouse event
         */
        @Override
        public void handleKeyEvent(KeyEvent ev) {
            processKeyEvent(ev);
        }

        /**
         * Handles focus events by forwarding it to
         * <code>processFocusEvent()</code>.
         * 
         * @param ev the Focus event
         */
        @Override
        public void handleFocusEvent(FocusEvent ev) {
            processFocusEvent(ev);
        }

        /**
         * Overridden so that this method returns the correct value even without
         * a peer.
         * 
         * @return the screen location of the button
         */
        @Override
        public Point getLocationOnScreen() {
            return CacioLabelPeer.this.getLocationOnScreen();
        }

        /**
         * Overridden so that the isShowing method returns the correct value for
         * the swing button, even if it has no peer on its own.
         * 
         * @return <code>true</code> if the button is currently showing,
         *         <code>false</code> otherwise
         */
        @Override
        public boolean isShowing() {
            boolean retVal = false;
            if (label != null)
                retVal = label.isShowing();
            return retVal;
        }

        /**
         * Overridden, so that the Swing button can create an Image without its
         * own peer.
         * 
         * @param w the width of the image
         * @param h the height of the image
         * 
         * @return an image
         */
        @Override
        public Image createImage(int w, int h) {
            return CacioLabelPeer.this.createImage(w, h);
        }

        @Override
        public Graphics getGraphics() {
            return CacioLabelPeer.this.getGraphics();
        }

        @Override
        public Container getParent() {
            Container par = null;
            if (label != null)
                par = label.getParent();
            return par;
        }
    }

    /**
     * Creates a new <code>SwingLabelPeer</code> for the specified AWT label.
     * 
     * @param label the AWT label
     * @param pwf the platform window factory
     */
    public CacioLabelPeer(Label label, PlatformWindowFactory pwf) {
        super(label, pwf);
    }

    @Override
    void initSwingComponent() {
        Label label = (Label) getAWTComponent();
        SwingLabel swingLabel = new SwingLabel(label);
        swingLabel.setText(label.getText());
        swingLabel.setOpaque(true);
        setSwingComponent(swingLabel);
        setAlignment(label.getAlignment());
    }

    /**
     * Sets the text of the label. This is implemented to set the text on the
     * Swing label.
     *
     * @param text the text to be set
     */
    @Override
    public void setText(String text)
    {
      ((JLabel) getSwingComponent().getJComponent()).setText(text);
    }

    /**
     * Sets the horizontal alignment of the label. This is implemented to
     * set the alignment on the Swing label.
     *
     * @param alignment the horizontal alignment
     *
     * @see Label#LEFT
     * @see Label#RIGHT
     * @see Label#CENTER
     */
    public void setAlignment(int alignment)
    {
      JLabel swingLabel = (JLabel) getSwingComponent().getJComponent();
      switch (alignment)
        {
        case Label.RIGHT:
          swingLabel.setHorizontalAlignment(JLabel.RIGHT);
          break;
        case Label.CENTER:
          swingLabel.setHorizontalAlignment(JLabel.CENTER);
          break;
        case Label.LEFT:
        default:
          swingLabel.setHorizontalAlignment(JLabel.LEFT);
          break;
        }
    }

}
