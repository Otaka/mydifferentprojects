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
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Concrete Command object implements TableModelListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * TableModelCommand applies for TableModel objects. It accepts JTable objects
 * to obtain TableModel too.
 * 
 * TableModelCommand is fired when TableModel is changed.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class TableModelCommand implements Command, TableModelListener {

    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public TableModelCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof TableModel) {
            ((TableModel) component).addTableModelListener(this);

        } else if (component instanceof JTable) {
            TableModel model = ((JTable) component).getModel();
            model.addTableModelListener(this);

        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        receiver.invokeMethodFromControllerNow(e);
    }
}