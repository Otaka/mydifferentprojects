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
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Concrete Command object implements WindowListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * WindowCommand applies for all {@link java.awt.Window} subclasses: JFrame,
 * JDialog, JWindow or any Window object.
 * 
 * WindowCommand is fired when a Window is opened, closing, closed, iconified,
 * deiconified, activated or deactivated.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.0, 2013/03/06
 * @since 1.0
 */
public class WindowCommand implements Command, WindowListener {

    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public WindowCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof Window) {
            ((Window) component).addWindowListener(this);
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void windowIconified(WindowEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void windowActivated(WindowEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        receiver.invokeMethodFromController(e);
    }
}