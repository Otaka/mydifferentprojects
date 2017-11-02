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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * Concrete Command object implements KeyListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * KeyCommand applies for all {@link javax.swing.JComponent} subclasses.
 * 
 * KeyCommand is fired when a key is typed, pressed or released.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.2, 2013/04/06
 * @since 1.0
 */
public class KeyCommand implements Command, KeyListener {

    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public KeyCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof JTextComponent) {
            ((JComponent) component).addKeyListener(this);
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        receiver.invokeMethodFromController(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        receiver.invokeMethodFromController(e);
    }
}