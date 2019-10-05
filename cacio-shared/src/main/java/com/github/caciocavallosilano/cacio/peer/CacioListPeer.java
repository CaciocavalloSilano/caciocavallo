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
package com.github.caciocavallosilano.cacio.peer;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.peer.ListPeer;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

final class CacioListPeer extends CacioComponentPeer<List, JScrollPane> implements ListPeer {

    private JList list;

    public CacioListPeer(List awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
    }

    @Override
    JScrollPane initSwingComponent() {
        list = new JList(new DefaultListModel());
        JScrollPane pane = new JScrollPane(list);
        return pane;
    }

    @Override
    void postInitSwingComponent() {
        super.postInitSwingComponent();
        // Add initial items.
        List theList = getAWTComponent();
        int itemCount = theList.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            add(theList.getItem(i), i);
        }
        setMultipleMode(theList.isMultipleMode());
        list.addListSelectionListener(new SelectionListener());
    }

    private DefaultListModel getModel() {
        DefaultListModel m = (DefaultListModel) list.getModel();
        return m;
    }

    @Override
    public void add(String item, int index) {
        if (index < 0) {
            getModel().addElement(item);
        } else {
            getModel().add(index, item);
        }
    }

    @Override
    public void delItems(int start, int end) {
        getModel().removeRange(start, end);
    }

    @Override
    public Dimension getMinimumSize(int rows) {
        FontMetrics fm = getFontMetrics(getAWTComponent().getFont());
        return new Dimension(20 + fm.stringWidth("0123456789abcde"),
                             (fm.getHeight() * rows));
    }

    @Override
    public Dimension getMinimumSize() {
        return getMinimumSize(5);
    }

    @Override
    public Dimension getPreferredSize(int rows) {
        Dimension minSize = getMinimumSize(5);
        Dimension actualSize = getSwingComponent().getPreferredSize();
        if (actualSize.width < minSize.width) actualSize.width = minSize.width;
        if (actualSize.height < actualSize.height) actualSize.height = minSize.height;
        return actualSize;
    }
    
    @Override
    public Dimension getPreferredSize() {
        int rows = getModel().getSize();
        return getPreferredSize((rows < 5) ? 5 : rows);
    }

    @Override
    public void makeVisible(int index) {
        list.ensureIndexIsVisible(index);
    }

    @Override
    public void select(int index) {
        list.setSelectedIndex(index);
    }

    @Override
    public void deselect(int index) {
        list.removeSelectionInterval(index, index);
    }

    @Override
    public void removeAll() {        
        ListSelectionModel m = list.getSelectionModel();
        m.clearSelection();
    }

    @Override
    public int[] getSelectedIndexes() {
        return list.getSelectedIndices();
    }

    @Override
    public void setMultipleMode(boolean multiple) {
        int mode;
        if (multiple) {
            mode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
        } else {
            mode = ListSelectionModel.SINGLE_SELECTION;
        }
        list.setSelectionMode(mode);
    }

    @Override
    protected void handleMouseEvent(MouseEvent e)
    {
        super.handleMouseEvent(e);
        if (e.getID() == MouseEvent.MOUSE_RELEASED && e.getClickCount() == 2) {
              getToolkit().getSystemEventQueue().postEvent(new ActionEvent(getAWTComponent(),ActionEvent.ACTION_PERFORMED, ""+getModel().getElementAt(list.locationToIndex(e.getPoint()))));
        }
    }

    public void setEnabled(boolean e) {
        super.setEnabled(e);
        list.setEnabled(e);
    }

    class SelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            for (int index = e.getFirstIndex(); index < e.getLastIndex(); index ++ )
            {
              getToolkit().getSystemEventQueue().postEvent(new ItemEvent(getAWTComponent(),ItemEvent.ITEM_STATE_CHANGED , getModel().getElementAt(index) ,list.isSelectedIndex(index)? ItemEvent.SELECTED:ItemEvent.DESELECTED));
            }
        }

    }

}
