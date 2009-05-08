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

import java.awt.Button;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.ButtonPeer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import sun.awt.ComponentAccessor;

class CacioButtonPeer extends CacioComponentPeer implements ButtonPeer {

    /**
     * A specialized Swing button to be used as AWT button.
     *
     * @author Roman Kennke (kennke@aicas.com)
     */
    class SwingButton extends JButton implements CacioSwingComponent {

        Button button;
        private boolean isFocused = false;

        SwingButton(Button button) {
            this.button = button;
            ComponentAccessor.setParent(this, button.getParent());
        }

        /**
         * Overridden so that this method returns the correct value even without
         * a peer.
         * 
         * @return the screen location of the button
         */
        @Override
        public Point getLocationOnScreen() {
            return CacioButtonPeer.this.getLocationOnScreen();
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
            if (button != null)
                retVal = button.isShowing();
            return retVal;
        }

        /**
         * Overridden, so that the Swing button can create an Image without its
         * own peer.
         * 
         * @param w
         *            the width of the image
         * @param h
         *            the height of the image
         * 
         * @return an image
         */
        @Override
        public Image createImage(int w, int h) {
            return CacioButtonPeer.this.createImage(w, h);
        }

        /**
         * Overridden, so that the Swing button can create a Graphics without
         * its own peer.
         * 
         * @return a graphics instance for the button
         */
        @Override
        public Graphics getGraphics() {
            return CacioButtonPeer.this.getGraphics();
        }

        /**
         * Returns this button.
         * 
         * @return this button
         */
        @Override
        public JComponent getJComponent() {
            return this;
        }

        /**
         * Handles mouse events by forwarding it to
         * <code>processMouseEvent()</code> after having retargetted it to
         * this button.
         * 
         * @param ev
         *            the mouse event
         */
        @Override
        public void handleMouseEvent(MouseEvent ev) {
            ev.setSource(this);
            processMouseEvent(ev);
        }

        /**
         * Handles mouse motion events by forwarding it to
         * <code>processMouseMotionEvent()</code> after having retargetted it
         * to this button.
         * 
         * @param ev
         *            the mouse motion event
         */
        @Override
        public void handleMouseMotionEvent(MouseEvent ev) {
            ev.setSource(this);
            processMouseMotionEvent(ev);
        }

        /**
         * Handles key events by forwarding it to <code>processKeyEvent()</code>
         * after having retargetted it to this button.
         * 
         * @param ev the mouse event
         */
        @Override
        public void handleKeyEvent(KeyEvent ev) {
            ev.setSource(this);
            processKeyBindings(ev, ev.getID() == KeyEvent.KEY_PRESSED);
        }

        /**
         * We copy+paste this from JComponent to get into processKeyBinding,
         * which is what we want really. But not the other stuff in
         * processKeyBindings in JComponent.
         */
        void processKeyBindings(KeyEvent e, boolean pressed) {
            // Get the KeyStroke
            KeyStroke ks;

            if (e.getID() == KeyEvent.KEY_TYPED) {
                ks = KeyStroke.getKeyStroke(e.getKeyChar());
            }
            else {
                ks = KeyStroke.getKeyStroke(e.getKeyCode(),e.getModifiers(),
                                          (pressed ? false:true));
            }

            /* Do we have a key binding for e? */
            processKeyBinding(ks, e, WHEN_FOCUSED, pressed);

        }

        public void requestFocus() {
            if (button != null) {
                button.requestFocus();
            }
        }

        /**
         * Handles focus events by forwarding it to
         * <code>processFocusEvent()</code>.
         * 
         * @param ev
         *            the Focus event
         */
        @Override
        public void handleFocusEvent(FocusEvent ev) {
            isFocused = ev.getID() == FocusEvent.FOCUS_GAINED;
            repaint();
        }

        public boolean hasFocus() {
            return isFocused;
        }
    }

    /**
     * Listens for ActionEvents on the Swing button and triggers corresponding
     * ActionEvents on the AWT button.
     *
     * @author Roman Kennke (kennke@aicas.com)
     */
    class SwingButtonListener implements ActionListener {

        /**
         * Receives notification when an action was performend on the button.
         * 
         * @param event
         *            the action event
         */
        @Override
        public void actionPerformed(ActionEvent event) {
            Button b = (Button) CacioButtonPeer.this.awtComponent;
            ActionListener[] l = b.getActionListeners();
            if (l.length == 0)
                return;
            ActionEvent ev = new ActionEvent(b, ActionEvent.ACTION_PERFORMED, b
                    .getActionCommand());
            for (int i = 0; i < l.length; ++i)
                l[i].actionPerformed(ev);
        }
      
    }

    /**
     * Constructs a new SwingButtonPeer.
     * 
     * @param theButton
     *            the AWT button for this peer
     */
    public CacioButtonPeer(Button theButton, PlatformWindowFactory pwf) {
        super(theButton, pwf);
    }

    @Override
    void initSwingComponent() {
        Button theButton = (Button) awtComponent;
        SwingButton button = new SwingButton(theButton);
        button.setText(theButton.getLabel());
        button.addActionListener(new SwingButtonListener());
        setSwingComponent(button);
        button.addNotify();
    }

    /**
     * Sets the label of the button. This call is forwarded to the setText method
     * of the managed Swing button.
     *
     * @param label the label to set
     */
    @Override
    public void setLabel(String label)
    {
      ((SwingButton) getSwingComponent()).setText(label);
    }

}
