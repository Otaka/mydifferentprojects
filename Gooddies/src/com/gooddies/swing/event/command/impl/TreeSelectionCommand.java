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
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Concrete Command object implements TreeSelectionListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * TreeSelectionCommand applies just for JTree objects.
 * 
 * TreeSelectionCommand is fired when the selection is changed in a
 * TreeSelectionModel.
 * 
 * TreeSelectionEvent does not inherit from ComponentEvent (as many Swing
 * components), it inherits directly from ObjectEvent and does not include the
 * getID() method to identify related events.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */

/**
 * @author root
 */
public class TreeSelectionCommand implements Command, TreeSelectionListener {

    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public TreeSelectionCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof JTree) {
            ((JTree) component).addTreeSelectionListener(this);
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        receiver.invokeMethodFromControllerNow(e);
    }
}