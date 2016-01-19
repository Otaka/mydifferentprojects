/*
 * © 2013 Incremedia - All rights reserved.
 * 
 * Use is subject to CPOL (Code Project Open License) terms.
 * A copy of the License is available at http://www.codeproject.com/info/cpol10.aspx
 */

package com.gooddies.swing.event.command;

/**
 * Command objects makes it easier to construct general components to execute
 * method calls.
 * 
 * Every Concrete Command objects implements a defined Listener from Java API
 * for Swing Components.
 * It is possible have one unique instance for a Concrete Command and add
 * components to a Map as follows:<br>
 * {@code Map actions = new HashMap<Object, Receiver>()}<br>
 * However, it is a better practice to add one listener to each GUI element.
 * @see <a href="http://stackoverflow.com/questions/5936261/how-to-add-action-listener-that-listens-to-multiple-buttons">How to add action listener that listens to multiple buttons</a>
 * 
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public interface Command {
    
    /**
     * Method that invokes an action from the Receiver object for every Concrete
     * Command object.
     * @throws IncompatibleCommandException 
     */
    public void execute();
}
