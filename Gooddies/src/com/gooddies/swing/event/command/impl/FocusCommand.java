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
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Concrete Command object implements FocusListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * FocusCommand for all {@link java.awt.Component} subclasses.
 * 
 * FocusCommand is fired when the focus component is gained or lost.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class FocusCommand implements Command, FocusListener {

    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public FocusCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof Component) {
            ((Component) component).addFocusListener(this);
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void focusLost(FocusEvent e) {
        receiver.invokeMethodFromController(e);
    }
}