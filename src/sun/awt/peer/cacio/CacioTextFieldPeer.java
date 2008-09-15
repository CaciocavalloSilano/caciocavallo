package sun.awt.peer.cacio;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.TextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.awt.im.InputMethodRequests;

import java.awt.peer.TextFieldPeer;

import javax.swing.JComponent;
import javax.swing.JPasswordField;

import javax.swing.KeyStroke;

import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;

class CacioTextFieldPeer extends CacioComponentPeer implements TextFieldPeer {

    CacioTextFieldPeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }

    @Override
    void initSwingComponent() {
        
        TextField textField = (TextField) awtComponent;
        SwingTextField swingComponent = new SwingTextField(textField);
        // avoid flickering while typing
        swingComponent.setDoubleBuffered(true);
        swingComponent.setText(textField.getText());
        swingComponent.setColumns(textField.getColumns());
        swingComponent.setEchoChar(textField.getEchoChar());
        swingComponent.setEditable(textField.isEditable());
        swingComponent.select(textField.getSelectionStart(),
                              textField.getSelectionEnd());
        
        swingComponent.addActionListener(new SwingTextFieldListener());
        
        setSwingComponent(swingComponent);
        swingComponent.addNotify();
    }
    
    // use of JPasswordField because of set/getEchoChar
    class SwingTextField extends JPasswordField implements CacioSwingComponent {

        TextField textField = null;
        private boolean isFocused = false;
        
        SwingTextField(TextField textField) {
            this.textField = textField;
            this.putClientProperty("JPasswordField.cutCopyAllowed",
                                   Boolean.TRUE);
        }
        
        @Override
        public JComponent getJComponent() {

            return this;
        }

        @Override
        public Point getLocationOnScreen() {
            
            return CacioTextFieldPeer.this.getLocationOnScreen();
        }
        
        @Override
        public Image createImage(int w, int h) {
            
            return CacioTextFieldPeer.this.createImage(w, h);
        }
        
        @Override
        public Graphics getGraphics() {
            return CacioTextFieldPeer.this.getGraphics();
        }
        
        @Override
        public void handleFocusEvent(FocusEvent ev) {
            
            isFocused = ev.getID() == FocusEvent.FOCUS_GAINED;
            
            Caret caret = this.getCaret();
            if (caret instanceof DefaultCaret) {
                DefaultCaret _caret = (DefaultCaret) caret;
                if (isFocused) {
                    _caret.focusGained(ev);
                } else {
                    _caret.focusLost(ev);
                }
            }
            
            repaint();
        }

        @Override
        public void handleKeyEvent(KeyEvent ev) {

            ev.setSource(this);
            processKeyBindings(ev, ev.getID() == KeyEvent.KEY_PRESSED);
            repaint();
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
        public Container getParent() {
            Container parent = null;
            if (textField != null)
                parent = textField.getParent();
            return parent;
        }
        
        @Override
        public void requestFocus() {
            
            if (this.textField != null) {
                this.textField.requestFocus();
            }
        }
        
        public boolean hasFocus() {
            return isFocused;
        }
        
        @Override
        public boolean isFocusable() {
            return true;
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
        
    }
 
    class SwingTextFieldListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            
            TextField textField =
                (TextField) CacioTextFieldPeer.this.awtComponent;
            
            ActionListener[] listeners = textField.getActionListeners();
            if (listeners.length == 0)
                return;
            
            ActionEvent ev =
                new ActionEvent(textField, 
                                ActionEvent.ACTION_PERFORMED,
                                event.getActionCommand());
            
            for (ActionListener listener : listeners) {
                listener.actionPerformed(ev);
            }
        }
    
    }
    
    /* ***** Peer specific implementation ***** */
    
    @Override
    public Dimension getMinimumSize(int columns) {
        
        
        
//        if (peerFont != null) {
//          FontMetrics fm = getFontMetrics(peerFont);
//          Dimension d = new Dimension();
//          d.width=len * fm.charWidth('W');
//          d.height = fm.getHeight()+2;
//          return d;
//          
//        } else {
            return ((SwingTextField) getSwingComponent()).getJComponent().
                        getMinimumSize();
//        }
    }

    @Override
    public Dimension getPreferredSize(int columns) {
        
        return ((SwingTextField) getSwingComponent()).getJComponent().
                    getPreferredSize();
    }

    @Override
    public void setEchoChar(char echoChar) {
        
        ((SwingTextField) getSwingComponent()).setEchoChar(echoChar);
    }

    @Override
    public int getCaretPosition() {
        
        return ((SwingTextField) getSwingComponent()).getCaretPosition();
    }

    @Override
    public InputMethodRequests getInputMethodRequests() {
        
        return ((SwingTextField) getSwingComponent()).getInputMethodRequests();
    }

    @Override
    public int getSelectionEnd() {
        
        return ((SwingTextField) getSwingComponent()).getSelectionEnd();
    }

    @Override
    public int getSelectionStart() {
        
        return ((SwingTextField) getSwingComponent()).getSelectionStart();
    }

    @Override
    public String getText() {
        
        return ((SwingTextField) getSwingComponent()).getText();
    }

    @Override
    public void select(int selStart, int selEnd) {
       
        ((SwingTextField) getSwingComponent()).select(selStart, selEnd);
    }

    @Override
    public void setCaretPosition(int pos) {
        
        ((SwingTextField) getSwingComponent()).setCaretPosition(pos);
    }

    @Override
    public void setEditable(boolean editable) {
        
        ((SwingTextField) getSwingComponent()).setEditable(editable);
    }

    @Override
    public void setText(String l) {
        
        ((SwingTextField) getSwingComponent()).setText(l);
    }
}
