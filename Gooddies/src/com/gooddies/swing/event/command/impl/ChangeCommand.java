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
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Concrete Command object implements ChangeListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * ChangeCommand applies for JTabbedPane, JSlider, JSpinner and JProgressBar
 * objects.
 * 
 * ActionCommand is fired when new tab is selected from JTabbedPane and when the
 * value is changed from JSlider, JSpinner or JProgressBar.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class ChangeCommand implements Command, ChangeListener {

    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public ChangeCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof  JTabbedPane) {
            ((JTabbedPane) component).addChangeListener(this);
    
        } else if (component instanceof  JSlider) {
            ((JSlider) component).addChangeListener(this);

        } else if (component instanceof  JSpinner) {
            ((JSpinner) component).addChangeListener(this);

        } else if (component instanceof  JProgressBar) {
            ((JProgressBar) component).addChangeListener(this);
 
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        receiver.invokeMethodFromControllerNow(e);
    }
}