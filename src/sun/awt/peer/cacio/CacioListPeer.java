package sun.awt.peer.cacio;

import java.awt.Dimension;
import java.awt.List;
import java.awt.peer.ListPeer;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

final class CacioListPeer extends CacioComponentPeer<List, JList> implements ListPeer {

    public CacioListPeer(List awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }

    @Override
    JList initSwingComponent() {
        JList list = new JList();
        return list;
    }

    @Override
    void postInitSwingComponent() {
        // Add initial items.
        List theList = getAWTComponent();
        int itemCount = theList.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            add(theList.getItem(i), i);
        }
    }

    private DefaultListModel getModel() {
        JList l = getSwingComponent();
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
        getSwingComponent().ensureIndexIsVisible(index);
    }

    @Override
    public void select(int index) {
        getSwingComponent().setSelectedIndex(index);
    }

    @Override
    public void deselect(int index) {
        getSwingComponent().removeSelectionInterval(index, index);
    }

    @Override
    public void removeAll() {
        JList l = getSwingComponent();
        ListSelectionModel m = l.getSelectionModel();
        m.clearSelection();
    }

    @Override
    public int[] getSelectedIndexes() {
        return getSwingComponent().getSelectedIndices();
    }

    @Override
    public void setMultipleMode(boolean multiple) {
        int mode;
        if (multiple) {
            mode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
        } else {
            mode = ListSelectionModel.SINGLE_SELECTION;
        }
        getSwingComponent().setSelectionMode(mode);
    }

}
