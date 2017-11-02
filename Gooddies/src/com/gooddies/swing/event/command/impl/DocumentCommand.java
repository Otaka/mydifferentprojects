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
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * Concrete Command object implements DocumentListener and its events.
 * This class has a Receiver object and invokes the invokeMethodFromController
 * method of the receiver.
 *
 * DocumentCommand applies for all {@link javax.swing.text.JTextComponent}
 * subclasses known: JEditorPane, JTextArea or JTextField (including
 * JFormattedTextField and JPasswordField).
 * This applies for JComboBox's ComboBoxEditor too.
 * 
 * DocumentCommand is fire when character(s) are added, changed, or deleted. 
 * 
 * DocumentEvent does not inherit from ComponentEvent (as many Swing components)
 * and does not include the getID() method to identify related events. 
 * DocumentEvent has got the method getType() that should be processed directly
 * in the invoker.
 *
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class DocumentCommand implements Command, DocumentListener {

    public static final int INSERT = 18001;
    public static final int REMOVE = 18002;
    public static final int CHANGE = 18003;
    private Receiver receiver;

    /**
     * Constructor.
     * @param receiver Receiver object
     */
    public DocumentCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        Object component = receiver.getComponent();
        if (component instanceof JTextComponent) {
            Document document = ((JTextComponent) component).getDocument();
            document.addDocumentListener(this);

        } else if (component instanceof JComboBox) {
            ComboBoxEditor editor = ((JComboBox) component).getEditor();
            JTextComponent textComponent = (JTextComponent) editor.getEditorComponent();
            Document document = textComponent.getDocument();
            document.addDocumentListener(this);

        } else {
            throw new IncompatibleCommandException(this, component);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        receiver.invokeMethodFromController(e, INSERT);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        receiver.invokeMethodFromController(e, REMOVE);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        receiver.invokeMethodFromController(e, CHANGE);
    }
}