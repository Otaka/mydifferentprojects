/*
 * © 2013 Incremedia - All rights reserved.
 * 
 * Use is subject to CPOL (Code Project Open License) terms.
 * A copy of the License is available at http://www.codeproject.com/info/cpol10.aspx
 */
package com.gooddies.swing.event.command;

import com.gooddies.swing.event.command.impl.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SwingEvent annotation applies a Command Design Pattern partially that
 * simplifies the using and the implemention of Listeners for Swing Components.
 * SwingEvent, in contrast to EventBus and other projects, adds listeners to
 * Swing Components dynamically with Annotations located in each Method.
 * Annotation includes sources, command and eventIds params that are related
 * with one o several Swing Components.<br>
 * SwingEvent supports multiple components and methods relationship.
 * @param sources List of Swing Components
 *     (e.g. {@code sources={"exitMenuItem","exitPopupMenuItem"}})
 * @param command Concrete Command class type
 *     (e.g. {@code command=ActionCommand.class})
 * @param eventIds List of events to be called by the Listener. Default value is
 *     "0" for Listeners with just one event implemented. For several methods,
 *     the event Ids should be included.
 *     (e.g. {@code eventIds={MouseEvent.MOUSE_PRESSED,MouseEvent.MOUSE_RELEASED}
 *     for a command=MouseCommand.class)
 * 
 * @see <a href="http://www.eventbus.org/index.html">EventBus Project</a>
 * 
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SwingEvent {
    /**
     * Default value for Listeners with just one event implemented.
     */
    public static final int DEFAULT_EVENT = 0;

    /**
     * One or more Swing Components.
     * @return The allowed sources
     */
    String[] sources();

    /**
     * Optional. Concrete Command class type. Default value is ActionCommand.
     * @return The allowed Concrete Command
     */
    Class<? extends Command> command() default ActionCommand.class;

    /**
     * Optional. List of events to be called by the Listener. Default value is
     * "0" for Listeners with just one method implemented. For several methods,
     * the event Ids should be included.
     * @return An array of events
     */
    int[] eventIds() default DEFAULT_EVENT;
}
