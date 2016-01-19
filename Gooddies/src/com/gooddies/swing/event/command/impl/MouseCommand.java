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
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Concrete Command object implements MouseListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * MouseCommand applies for all {@link java.awt.Component} subclasses and 
 * TrayIcon.
 * 
 * MouseCommand is fired when Mouse is clicked, pressed, released, entered or
 * exited.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class MouseCommand implements Command, MouseListener {

    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public MouseCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof Component) {
            ((Component) component).addMouseListener(this);

        } else if (component instanceof TrayIcon) {
            ((TrayIcon) component).addMouseListener(this);

        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        receiver.invokeMethodFromController(e);
    }
}