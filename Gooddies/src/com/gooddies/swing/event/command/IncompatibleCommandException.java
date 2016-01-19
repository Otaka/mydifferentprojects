/*
 * © 2013 Incremedia - All rights reserved.
 * 
 * Use is subject to CPOL (Code Project Open License) terms.
 * A copy of the License is available at http://www.codeproject.com/info/cpol10.aspx
 */

package com.gooddies.swing.event.command;

/**
 * Thrown when a component is incompatible with a Concrete Command.
  * 
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.1
 */
public class IncompatibleCommandException extends RuntimeException {

    public IncompatibleCommandException(String message) {
        super(message);
    }

    public IncompatibleCommandException(Command command, Object component) {
        this("Incompatible " + component.getClass().getName() +
                " class with " + command.getClass().getSimpleName());
    }
}