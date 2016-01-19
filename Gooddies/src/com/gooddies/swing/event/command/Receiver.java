/*
 * © 2013 Incremedia - All rights reserved.
 * 
 * Use is subject to CPOL (Code Project Open License) terms.
 * A copy of the License is available at http://www.codeproject.com/info/cpol10.aspx
 */

package com.gooddies.swing.event.command;

import java.awt.AWTEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Receiver class, called by Concrete Command classes, invokes a method from
 * Controller defined with the SwingEvent annotation. Receiver executes this
 * method from a Concrete Command that implements a Listener for a target
 * Component.
 * 
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class Receiver {
    private Object component;
    private Object controller;
    private Method method;
    private int[] eventIds;

    /**
     * Constructor
     * @param component Target Component
     * @param controller Controller class where methods are located
     * @param method Method to invoke
     * @param eventIds List of events identifier that will be executed
     */
    public Receiver(Object component, Object controller, Method method, int[] eventIds) {
        this.component = component;
        this.controller = controller;
        this.method = method;
        this.eventIds = eventIds;
    }

    /**
     * Invoke method located in the Controller getting an AWTEvent object that
     * is sent to Controller's method
     * @param eventObject AWTEvent object
     */
    public void invokeMethodFromController(AWTEvent eventObject) {
        invokeMethodFromController(eventObject, eventObject.getID());
    }

    /**
     * Invoke method located in the Controller getting an Event object and an
     * event type identifier. Event object is sent to Controller's method
     * @param eventObject Event object
     * @param eventType Event type identifier that will be executed
     */
    public void invokeMethodFromController(Object eventObject, int eventType) {
        for (int eventId : eventIds) {
            if(eventType == eventId) {
                invokeMethodFromControllerNow(eventObject);
            }
        }
    }

    /**
     * Invoke method located in the Controller getting an Event object that
     * is sent to Controller's method
     * @param eventObject Event object
     */
    public void invokeMethodFromControllerNow(Object eventObject) {
        try {
            try {
                method.invoke(controller);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IllegalArgumentException iae) {
            try {
                method.invoke(controller, eventObject);
            }catch(IllegalArgumentException ex){
                Class param=method.getParameterTypes()[0];
                throw new RuntimeException("Event invocation problem. Expected one argument "+param.getName()+" but found "+eventObject.getClass().getName());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Invocation problem: " + e);
            }
        }
    }

    /**
     * Retrieve the target swing component
     * @return Target component
     */
    public Object getComponent() {
        return component;
    }

    /**
     * Retrieve list of events identifier that will be executed
     * @return List of events
     */
    public int[] getEventIds() {
        return eventIds;
    }
}