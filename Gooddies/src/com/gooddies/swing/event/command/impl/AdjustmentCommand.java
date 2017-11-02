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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * Concrete Command object implements AdjustmentListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * AdjustmentCommand applies for JScrollBar and JScrollPane.
 * 
 * AdjustmentCommand is fired when the JScrollBar adjustment is made. 
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class AdjustmentCommand implements Command, AdjustmentListener {

    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public AdjustmentCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof JScrollBar) {
            ((JScrollBar) component).addAdjustmentListener(this);

        } else if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            JScrollBar hScrollBar = scrollPane.getHorizontalScrollBar();
            JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
            hScrollBar.addAdjustmentListener(this);
            vScrollBar.addAdjustmentListener(this);
            
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        receiver.invokeMethodFromControllerNow(e);
    }
}