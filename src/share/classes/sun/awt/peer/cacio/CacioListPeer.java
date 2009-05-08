package sun.awt.peer.cacio;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.List;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.ListPeer;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

final class CacioListPeer extends CacioComponentPeer implements ListPeer {

    class SwingList extends JList implements CacioSwingComponent {

        List list;

        SwingList(List l) {
            super(new DefaultListModel());
            list = l;
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
        
        /**
         * Overridden so that this method returns the correct value even without
         * a peer.
         * 
         * @return the screen location of the button
         */
        @Override
        public Point getLocationOnScreen() {
            return CacioListPeer.this.getLocationOnScreen();
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
            if (list != null) {
                retVal = list.isShowing();
            }
            return retVal;
        }

        /**
         * Overridden, so that the Swing component can create an Image
         * without its own peer.
         * 
         * @param w the width of the image
         * @param h the height of the image
         *
         * @return an image
         */
        @Override
        public Image createImage(int w, int h) {
            return CacioListPeer.this.createImage(w, h);
        }

        /**
         * Overridden, so that the Swing component can create a Graphics
         * without its own peer.
         * 
         * @return a graphics instance for the swing component
         */
        @Override
        public Graphics getGraphics() {
            return CacioListPeer.this.getGraphics();
        }

        @Override
        public Container getParent() {
            Container par = null;
            if (list != null)
                par = list.getParent();
            return par;
        }
    }

    public CacioListPeer(Component awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }

    @Override
    void initSwingComponent() {
        List theList = (List) awtComponent;
        SwingList list = new SwingList(theList);
        setSwingComponent(list);
        // Add initial items.
        int itemCount = theList.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            add(theList.getItem(i), i);
        }
        list.addNotify();
    }

    private DefaultListModel getModel() {
        JList l = ((SwingList) getSwingComponent());
        DefaultListModel m = (DefaultListModel) l.getModel();
        return m;
    }

    @Override
    public void add(String item, int index) {
        
        getModel().add(index, item);
    }

    @Override
    public void delItems(int start, int end) {
        getModel().removeRange(start, end);
    }

    @Override
    public Dimension getMinimumSize(int rows) {
        return super.getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize(int rows) {
        return super.getPreferredSize();
    }

    @Override
    public void makeVisible(int index) {
        ((SwingList) getSwingComponent()).ensureIndexIsVisible(index);
    }

    @Override
    public void select(int index) {
        ((SwingList) getSwingComponent()).setSelectedIndex(index);
    }

    @Override
    public void deselect(int index) {
        ((SwingList) getSwingComponent()).removeSelectionInterval(index, index);
    }

    @Override
    public void removeAll() {
        JList l = ((SwingList) getSwingComponent());
        ListSelectionModel m = l.getSelectionModel();
        m.clearSelection();
    }

    @Override
    public int[] getSelectedIndexes() {
        return ((SwingList) getSwingComponent()).getSelectedIndices();
    }

    @Override
    public void setMultipleMode(boolean multiple) {
        int mode;
        if (multiple) {
            mode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
        } else {
            mode = ListSelectionModel.SINGLE_SELECTION;
        }
        ((SwingList) getSwingComponent()).setSelectionMode(mode);
    }

}
