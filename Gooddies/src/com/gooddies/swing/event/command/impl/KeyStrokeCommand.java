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
import java.awt.event.KeyEvent;
import java.util.UUID;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Concrete Command object implements {@link javax.swing.Action} and its
 * actionPerformed event via {@link javax.swing.AbstractAction} class.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * KeyStrokeCommand applies for all {@link javax.swing.JComponent} subclasses.
 * 
 * KeyStrokeCommand is fired when Key or equivalent input device is typed,
 * pressed or released with modifiers (alt, shift, control, meta, altGraph or
 * combined)<br><br>
 * 
 * KeyStrokeCommand redefine params into the SwingEvent annotation as follow:
 * <b>sources</b> List of Swing Components with a common event.
 * <b>command</b> KeyStrokeCommand class type
 * <b>eventIds</b> List of events to be added at the KeyStroke, they are:
 * <ul>
 * <li>First item: represents the event Id of the Swing Component.</li>
 * <li>Second item: represents the Key code. List of codes is available at 
 * KeyEvent class (e.g. KeyEvent.VK_A, KeyEvent.VK_ESCAPE or "M").</li>
 * <li>More items: represents the modifiers. List of modifiers is available at
 * InputEvent or KeyEvent classes (e.g. InputEvent.CTRL_DOWN_MASK,
 * KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)</li>
 * </ul><br>
 * In the next example <b>invokeMethod()</b> is fired when the Escape key is
 * pressed over JPanel1 as long as it gains the focus:<br><br>
 * <pre>{@code
 * (@)SwingEvent(sources="JPanel1", command=KeyStrokeCommand.class,
 *     eventIds={JComponent.WHEN_IN_FOCUSED_WINDOW, KeyEvent.VK_ESCAPE})
 * public void invokeMethod() {}
 * }</pre>
 * 
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.0b, 2013/04/06
 * @since 1.2
 */
public class KeyStrokeCommand extends AbstractAction implements Command {

    /**
     * Execute a method tagged with SwingEvent annotation, when the Listener's
     * Event is fired.
     */
    private Receiver receiver;

    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public KeyStrokeCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    /** {@inheritDoc}
     */
    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof JComponent) {
            JComponent jComponent = (JComponent) component;
            int[] eventIds = receiver.getEventIds();
            mapEvents(jComponent, eventIds);
        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        receiver.invokeMethodFromControllerNow(e);
    }
    
    /*
     * Create a new KeyStroke to be added to the JComponent's ActionMap.
     */
    private void mapEvents(JComponent jComponent, int[] eventIds) {
        int aCondition = JComponent.UNDEFINED_CONDITION;
        int keyCode = KeyEvent.CHAR_UNDEFINED;
        int modifiers = KeyEvent.VK_UNDEFINED;

        for(int i = 0; i < eventIds.length; i++) {
            if (i == 0) {
                aCondition = eventIds[i];
            } else if (i == 1) {
                keyCode = eventIds[i];
            } else {
                modifiers = modifiers | eventIds[i];
            }
        }
        InputMap inputMap = jComponent.getInputMap(aCondition);
        String uuId = UUID.randomUUID().toString();
        KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, modifiers);
        inputMap.put(stroke, uuId);

        ActionMap actionMap = jComponent.getActionMap();
        Action defaultAction = actionMap.get(Action.DEFAULT);
        if (defaultAction != null) {
            actionMap.put(uuId, defaultAction);
        } else {
            actionMap.put(uuId, this);
        }
    }
}