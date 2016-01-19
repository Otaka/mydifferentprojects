/*
 * © 2013 Incremedia - All rights reserved.
 * 
 * Use is subject to CPOL (Code Project Open License) terms.
 * A copy of the License is available at http://www.codeproject.com/info/cpol10.aspx
 */
package com.gooddies.swing.event.command.impl;

import com.gooddies.swing.event.command.Command;
import com.gooddies.swing.event.command.IncompatibleCommandException;
import com.gooddies.swing.event.command.Receiver;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Concrete Command object implements ItemListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * ItemCommand applies for all {@link java.awt.ItemSelectable} subclasses
 * as JComboBox.
 * 
 * ItemCommand is fired when a entry is selected from the a list as JComboBox.
 * 
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class ItemCommand implements Command, ItemListener {

    private Receiver receiver;

    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public ItemCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof ItemSelectable) {
            ((ItemSelectable) component).addItemListener(this);
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        int stateChange = e.getStateChange();
        if (stateChange == ItemEvent.SELECTED) {
            receiver.invokeMethodFromControllerNow(e);
        }
    }
}