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
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Concrete Command object implements ActionListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * ActionCommand applies for all {@link javax.swing.AbstractButton} subclasses
 * known: JButton, JToggleButton, JCheckBox, JRadioButton, JMenuItem, JMenu,
 * JCheckBoxMenuItem or JRadioButtonMenuItem.
 * It applies for JTextField, JComboBox, Timer, TrayIcon and JFileChooser
 * objects too.
 * 
 * ActionCommand is fired when AbstractButton is pressed or toggled; when return
 * button is pressed on JTextField or focus switches to another component; when 
 * a new entry is selected from JComboBox's list or return button is pressed;
 * and when Timer, TrayIcon and JFileChooser are executed.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.2, 2013/04/06
 * @since 1.0
 */
public class ActionCommand implements Command, ActionListener {

    private Receiver receiver;

    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public ActionCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof AbstractButton) {
            ((AbstractButton) component).addActionListener(this);
        } else if (component instanceof JTextField) {
            ((JTextField) component).addActionListener(this);
        } else if (component instanceof JComboBox) {
            ((JComboBox) component).addActionListener(this);
        } else if (component instanceof Timer) {
            ((Timer) component).addActionListener(this);
        } else if (component instanceof TrayIcon) {
            ((TrayIcon) component).addActionListener(this);
        } else if (component instanceof JFileChooser) {
            ((JFileChooser) component).addActionListener(this);
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        receiver.invokeMethodFromControllerNow(e);
    }
}