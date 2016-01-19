/*
 * © 2013 Incremedia - All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gooddies.swing.event.command.impl;

import com.gooddies.swing.event.command.Command;
import com.gooddies.swing.event.command.IncompatibleCommandException;
import com.gooddies.swing.event.command.Receiver;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Group and centralize and group control of functionality for several components.
 * 
 * Concrete Command object implements {@link javax.swing.Action} and its
 * actionPerformed event via {@link javax.swing.AbstractAction} class. This
 * class has a Receiver object and invokes the invokeMethodFromController method
 * of the receiver.
 * 
 * AbstractActionCommand applies for all {@link javax.swing.AbstractButton},
 * {@link javax.swing.JTextField} and {@link javax.swing.JCheckBox} subclasses.<br><br>
 * 
 * AbstractActionCommand redefine params into the SwingEvent annotation as follow:<br>
 * <b>sources</b> List of Swing Components supporting setAction(javax.swing.Action)
 * method<br>
 * <b>command</b> AbstractActionCommand class type<br>
 * <b>eventIds</b> Action group key<br><br>
 * 
 * ActionEvent's source will be Action grouping several components.
 * In the next example jButton1 and jMenuItem1 are grouped and
 * <b>bindCommonAction(java.awt.event.ActionEvent)</b> is fired when any of 
 * these components is pressed. Action can be retrieved from e.getSource()
 * method:<br><br>
 * <pre>{@code
 * int ACTION_GROUP_KEY = 1;
 * (@)SwingEvent(sources={"JButton1","JMenuItem1"},
 *         command=AbstractActionCommand.class, eventIds=ACTION_GROUP_1_KEY)
 * public void bindCommonAction(ActionEvent e) {
 *     Action actionGroup = (AbstractAction)e.getSource();
 * }
 * }</pre>
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.0b, 2013/04/06
 * @since 1.2
 */
public class AbstractActionCommand extends AbstractAction implements Command {

    /**
     * Execute a method tagged with SwingEvent annotation, when the Listener's
     * Event is fired.
     */
    private Receiver receiver;

    /**
     * Provide Action mappings from a defined component.
     */
    private static ActionMap actionMap = new ActionMap();

    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public AbstractActionCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    /** {@inheritDoc}
     */
    @Override
    public void execute() {
       
        int[] eventIds = receiver.getEventIds();
        Action action = buildActionFromGroupKey(eventIds);
        
        Object component = receiver.getComponent();
        if (component instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem)component;
            String text = menuItem.getText();
            int mnemonic = menuItem.getMnemonic();
            KeyStroke accelerator = menuItem.getAccelerator();
            menuItem.setAction(action);
            menuItem.setText(text);
            menuItem.setAccelerator(accelerator);
            menuItem.setMnemonic(mnemonic);

        } else if (component instanceof AbstractButton) {
            AbstractButton abstractButton = (AbstractButton)component;
            String text = abstractButton.getText();
            int mnemonic = abstractButton.getMnemonic();
            abstractButton.setAction(action);
            abstractButton.setText(text);
            abstractButton.setMnemonic(mnemonic);

        } else if (component instanceof JTextField) {
            JTextField textField = (JTextField)component;
            textField.setAction(action);

        } else if (component instanceof JComboBox) {
            JComboBox comboBox = (JComboBox)component;
            comboBox.setAction(action);

        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        e.setSource(this);
        receiver.invokeMethodFromControllerNow(e);
    }

    /**
     * Retrieve the Action for a group key into the internal ActionMap.
     * @param groupKey
     * @return Action for a group key
     */
    public static Action findAction(Integer groupKey) {
        return actionMap.get(groupKey);
    }

    /**
     * Retrieve Action for a group key. If the group key it does not exist,
     * create a new Action group.
     * @param eventIds
     * @return Action for a group key
     */
    private Action buildActionFromGroupKey(int[] eventIds) {
        Integer groupKey = 0;
        if (eventIds.length == 1) {

            // Get only one group indicator.        
            groupKey = eventIds[0];
            Action action = actionMap.get(groupKey);
            if (action == null) {
                action = this;
                actionMap.put(groupKey, action);
            }
        }
        return findAction(groupKey);
    }
}