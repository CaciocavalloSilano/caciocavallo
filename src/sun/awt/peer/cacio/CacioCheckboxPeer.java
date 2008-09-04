package sun.awt.peer.cacio;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.CheckboxPeer;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

class CacioCheckboxPeer extends CacioComponentPeer implements CheckboxPeer {

    public CacioCheckboxPeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates a new SwingCheckboxPeer instance.
     */
    public void initSwingComponent() {

        Checkbox checkbox = (Checkbox) getAWTComponent();
        SwingCheckbox swingCheckbox = new SwingCheckbox(checkbox);
        swingCheckbox.addItemListener(new SwingCheckboxListener(checkbox));
        setSwingComponent(swingCheckbox);
        setLabel(checkbox.getLabel());
        setState(checkbox.getState());
        swingCheckbox.addNotify();
    }

    /**
     * A spezialized Swing checkbox used to paint the checkbox for the AWT
     * checkbox.
     */
    private class SwingCheckbox extends JCheckBox
                                implements CacioSwingComponent {

        Checkbox checkbox;

        private boolean isFocused;

        SwingCheckbox(Checkbox checkbox) {
            this.checkbox = checkbox;
        }

        /**
         * Returns this checkbox.
         * 
         * @return <code>this</code>
         */
        public JComponent getJComponent() {
            return this;
        }

        /**
         * Handles mouse events by forwarding it to
         * <code>processMouseEvent()</code>.
         * 
         * @param ev
         *            the mouse event
         */
        public void handleMouseEvent(MouseEvent ev) {
            ev.setSource(this);
            processMouseEvent(ev);
        }

        /**
         * Handles mouse motion events by forwarding it to
         * <code>processMouseMotionEvent()</code>.
         * 
         * @param ev
         *            the mouse motion event
         */
        public void handleMouseMotionEvent(MouseEvent ev) {
            ev.setSource(this);
            processMouseMotionEvent(ev);
        }

        /**
         * Handles key events by forwarding it to <code>processKeyEvent()</code>.
         * 
         * @param ev
         *            the mouse event
         */
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

        /**
         * Handles focus events by forwarding it to
         * <code>processFocusEvent()</code>.
         * 
         * @param ev
         *            the Focus event
         */
        public void handleFocusEvent(FocusEvent ev) {
            isFocused = ev.getID() == FocusEvent.FOCUS_GAINED;
            repaint();
        }

        public boolean hasFocus() {
            return isFocused;
        }

        /**
         * Overridden so that this method returns the correct value even without
         * a peer.
         * 
         * @return the screen location of the button
         */
        public Point getLocationOnScreen() {
            return CacioCheckboxPeer.this.getLocationOnScreen();
        }

        /**
         * Overridden so that the isShowing method returns the correct value for
         * the swing button, even if it has no peer on its own.
         * 
         * @return <code>true</code> if the button is currently showing,
         *         <code>false</code> otherwise
         */
        public boolean isShowing() {
            boolean retVal = false;
            if (checkbox != null)
                retVal = checkbox.isShowing();
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
        public Image createImage(int w, int h) {
            return CacioCheckboxPeer.this.createImage(w, h);
        }

        public Graphics getGraphics() {
            return CacioCheckboxPeer.this.getGraphics();
        }

        public Container getParent() {
            Container par = null;
            if (checkbox != null)
                par = checkbox.getParent();
            return par;
        }

        public void requestFocus() {
            CacioCheckboxPeer.this.getAWTComponent().requestFocus();
        }
    }

    /**
     * Listens for ActionEvents on the Swing button and triggers corresponding
     * ActionEvents on the AWT button.
     */
    class SwingCheckboxListener implements ItemListener {
        Checkbox awtCheckbox;

        SwingCheckboxListener(Checkbox checkbox) {
            awtCheckbox = checkbox;
        }

        /**
         * Receives notification when an action was performend on the button.
         * 
         * @param event
         *            the action event
         */
        public void itemStateChanged(ItemEvent event) {
            awtCheckbox.setState(event.getStateChange() == ItemEvent.SELECTED);
            ItemListener[] l = awtCheckbox.getItemListeners();
            if (l.length == 0)
                return;
            ItemEvent ev = new ItemEvent(awtCheckbox,
                    ItemEvent.ITEM_STATE_CHANGED, awtCheckbox, event
                            .getStateChange());
            for (int i = 0; i < l.length; ++i)
                l[i].itemStateChanged(ev);
        }
    }

    public void setCheckboxGroup(CheckboxGroup group) {
        // TODO: Implement this.
    }

    public void setLabel(String label) {
        ((JToggleButton) getSwingComponent()).setText(label);
    }

    public void setState(boolean state) {
        ((JToggleButton) getSwingComponent()).setSelected(state);
    }
}
