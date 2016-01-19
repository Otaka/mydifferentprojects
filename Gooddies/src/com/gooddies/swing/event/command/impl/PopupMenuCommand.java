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
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Concrete Command object implements PopupMenuListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * PopupMenuCommand applies for JPopupMenu and JComboBox objects.
 * 
 * PopupMenuCommand is fired when JPopupMenu or JComboBox will become visible,
 * invisible, or canceled without a selection.
 *
 * PopupMenuEvent does not inherit from ComponentEvent (as many Swing
 * components), it inherits directly from ObjectEvent and does not include the
 * getID() method to identify related events.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class PopupMenuCommand implements Command, PopupMenuListener {

    public static final int WILL_BECOME_VISIBLE = 18007;
    public static final int WILL_BECOME_INVISIBLE = 18008;
    public static final int CANCELED = 18009;
    
    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public PopupMenuCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof  JPopupMenu) {
            ((JPopupMenu) component).addPopupMenuListener(this);

        } else if (component instanceof  JComboBox) {
            ((JComboBox) component).addPopupMenuListener(this);

        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        receiver.invokeMethodFromController(e, WILL_BECOME_VISIBLE);
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        receiver.invokeMethodFromController(e, WILL_BECOME_INVISIBLE);
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        receiver.invokeMethodFromController(e, CANCELED);
    }
}