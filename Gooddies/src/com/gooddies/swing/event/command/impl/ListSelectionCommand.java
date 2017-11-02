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
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Concrete Command object implements ListSelectionListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * ListSelectionCommand applies for JList and ListSelectionModel.
 * These components do not have a common parent relationed to
 * {@link javax.swing.event.ListSelectionListener}
 * ListSelectionCommand applies for JTable's ListSelectionModel too.
 * 
 * ListSelectionCommand is fired when a new entry is selected from JList or
 * ListSelectionModel.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class ListSelectionCommand implements Command, ListSelectionListener {

    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public ListSelectionCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof JList) {
            ((JList) component).addListSelectionListener(this);

        } else if (component instanceof ListSelectionModel) {
            ((ListSelectionModel) component).addListSelectionListener(this);

        } else if (component instanceof JTable) {
            ListSelectionModel rowSelectionModel = ((JTable) component).getSelectionModel();
            rowSelectionModel.addListSelectionListener(this);

        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            receiver.invokeMethodFromControllerNow(e);
        }
    }
}