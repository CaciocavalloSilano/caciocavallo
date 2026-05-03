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

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxPeer;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

class CacioCheckboxPeer extends CacioComponentPeer<Checkbox, JPanel>
                        implements CheckboxPeer {

    private JToggleButton toggleButton;

    public CacioCheckboxPeer(Checkbox awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates a new SwingCheckboxPeer instance.
     */
    @Override
    public JPanel initSwingComponent() {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        if (getAWTComponent().getCheckboxGroup() == null) {
            toggleButton = new JCheckBox();
        } else {
            toggleButton = new JRadioButton();
        }
        panel.add(toggleButton);
        return panel;
    }

    @Override
    void postInitSwingComponent() {
        super.postInitSwingComponent();
        toggleButton.addItemListener(new SwingCheckboxListener());

        Checkbox checkbox = getAWTComponent();
        setLabel(checkbox.getLabel());
        setState(checkbox.getState());
    }

    /**
     * Listens for ActionEvents on the Swing button and triggers corresponding
     * ActionEvents on the AWT button.
     */
    class SwingCheckboxListener implements ItemListener {

        /**
         * Receives notification when an action was performend on the button.
         * 
         * @param event
         *            the action event
         */
        public void itemStateChanged(ItemEvent event) {
            // Radiobuttons don't fire DESELECTED events...
            Checkbox awtCheckbox = getAWTComponent();
            if (event.getStateChange() == ItemEvent.DESELECTED
                && awtCheckbox.getCheckboxGroup() != null) {
                return;
            }
            awtCheckbox.setState(event.getStateChange() == ItemEvent.SELECTED);
            ItemListener[] l = awtCheckbox.getItemListeners();
            if (l.length == 0)
                return;
            ItemEvent ev = new ItemEvent(awtCheckbox,
                    ItemEvent.ITEM_STATE_CHANGED, awtCheckbox.getLabel(), event
                            .getStateChange());
            for (int i = 0; i < l.length; ++i)
                l[i].itemStateChanged(ev);
        }
    }

    @Override
    public void setCheckboxGroup(CheckboxGroup group) {
        if (group == null) {
            toggleButton = new JCheckBox();
        } else {
            toggleButton = new JRadioButton();
        }
        getSwingComponent().removeAll();
        getSwingComponent().add(toggleButton);
        postInitSwingComponent();
    }

    public void setLabel(String label) {
        toggleButton.setText(label);
    }

    public void setState(boolean state) {
        toggleButton.setSelected(state);
    }

    @Override
    void setEnabledImpl(boolean e) {
        super.setEnabledImpl(e);
        toggleButton.setEnabled(e);
    }
}
