package sun.awt.peer.cacio;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxPeer;

import javax.swing.JCheckBox;

class CacioCheckboxPeer extends CacioComponentPeer<Checkbox, JCheckBox>
                        implements CheckboxPeer {

    public CacioCheckboxPeer(Checkbox awtC, PlatformWindowFactory pwf) {
        super(awtC, pwf);
        // TODO Auto-generated constructor stub
    }

    /**
     * Creates a new SwingCheckboxPeer instance.
     */
    @Override
    public JCheckBox initSwingComponent() {

        Checkbox checkbox = (Checkbox) getAWTComponent();
        JCheckBox jcheckbox = new JCheckBox();
        jcheckbox.addItemListener(new SwingCheckboxListener());
        return jcheckbox;
    }

    @Override
    void postInitSwingComponent() {
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
            Checkbox awtCheckbox = getAWTComponent();
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
        getSwingComponent().setText(label);
    }

    public void setState(boolean state) {
        getSwingComponent().setSelected(state);
    }
}
