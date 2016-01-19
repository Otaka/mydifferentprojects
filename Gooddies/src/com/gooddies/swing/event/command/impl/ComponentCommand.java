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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Concrete Command object implements ComponentListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * ComponentCommand for all {@link java.awt.Component} subclasses.
 * 
 * ComponentCommand is fired when Component is resized, moved, shown or hidden.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.0, 2013/04/06
 * @since 1.2
 */
public class ComponentCommand implements Command, ComponentListener {

    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public ComponentCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof Component) {
            ((Component) component).addComponentListener(this);
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        receiver.invokeMethodFromController(e);
    }
}