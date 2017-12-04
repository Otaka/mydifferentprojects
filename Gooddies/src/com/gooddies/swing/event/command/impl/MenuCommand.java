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
import javax.swing.JMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * Concrete Command object implements MenuListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * MenuCommand applies for JMenu.
 * 
 * MenuCommand is fired when JMenu becomes visible, invisible, or canceled
 * without a selection.
 *
 * MenuEvent does not inherit from AWTEvent (as many Swing components), it
 * inherits directly from ObjectEvent and does not include the getID() method to
 * identify related events.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class MenuCommand implements Command, MenuListener {

    public static final int SELECTED = 18004;
    public static final int DESELECTED = 18005;
    public static final int CANCELED = 18006;
    
    private Receiver receiver;
    
    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public MenuCommand(Receiver receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof  JMenu) {
            ((JMenu) component).addMenuListener(this);
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void menuSelected(MenuEvent e) {
        receiver.invokeMethodFromController(e, SELECTED);
    }

    @Override
    public void menuDeselected(MenuEvent e) {
        receiver.invokeMethodFromController(e, DESELECTED);
    }

    @Override
    public void menuCanceled(MenuEvent e) {
        receiver.invokeMethodFromController(e, CANCELED);
    }
}