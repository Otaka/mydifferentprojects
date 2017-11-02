/*
 * © 2013 Incremedia - All rights reserved.
 * 
 * Use is subject to CPOL (Code Project Open License) terms.
 * A copy of the License is available at http://www.codeproject.com/info/cpol10.aspx
 */
package com.gooddies.swing.event.command;

import com.gooddies.exceptions.ValidationException;
import java.awt.Window;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * The CommandBinder class enumerates all methods from the Controller object and
 * processes SwingEvent annotations.<br>
 * Methods with SwingEvent annotation are linked to:
 * <ul>
 * <li>Components from View object.</li>
 * <li>Controller where methods are located.</li>
 * <li>One or several event type identifiers.</li>
 * </ul>
 * The previous information is stored in a Receiver object. This object is bound
 * to a Concrete Command object that implements a Listener for the defined
 * Component.
 * 
 * Annotations processing was based on the "Core Java" book by Cay Horstmann and
 * Gary Cornell. Proxy object used by the authors was replaced due to
 * performance improvements.
 * 
 * @see <a href="http://www.horstmann.com/corejava/cj7v2ch13ex.pdf">Annotations,
 * chapter 13. "Core Java"</a>
 * 
 * @author Sonny Benavides (incremedia@yahoo.com.co)
 * @version 1.1, 2013/03/06
 * @since 1.0
 */
public class SwingEventCommandBinder {

    /**
     * Provides the logging capabilities for runtime exceptions.
     */
    private static final Logger LOGGER = Logger.getLogger(SwingEventCommandBinder.class.getName());

    /**
     * Processes all annotations for a target Component located in the same
     * object.
     * @param viewAndController Object that contains Swing Components and
     *        methods that may have SwingEvent annotations.
     */
    public static void processAnnotations(Object viewAndController) {
        processAnnotations(viewAndController, viewAndController);
    }

    private static Method[] getSwingEventMethods(Class clazz) {
        List<Method> methods = new ArrayList<>();
        while (clazz != null) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method m : declaredMethods) {
                if (m.isAnnotationPresent(SwingEvent.class)) {
                    if (!m.isAccessible()) {
                        m.setAccessible(true);
                    }
                    methods.add(m);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return methods.toArray(new Method[methods.size()]);
    }

    /**
     * Processes all Controller annotations for a target Component located in
     * the View.
     * Update v1.1: getDeclaredMethods operation was reeplace by getMethods to
     * retrieve all methods inclusive if they are inherited.
     * @param view View that contains Swing Components for the GUI
     * @param controller Controller whose methods may have SwingEvent annotations
     */
    public static void processAnnotations(Object view, Object controller) {
        Object tw=view;
        if(view instanceof JFrame){
            tw=((JFrame)tw).getRootPane();
        }else if(view instanceof JDialog){
            tw=((JDialog)tw).getRootPane();
        }
        if (tw instanceof JComponent) {
            JComponent component=(JComponent)tw;
            if(component.getClientProperty("annotationController")!=null){
                throw new ValidationException("Cannot attach SwingEvents one more time to "+tw.toString());
            }
            component.putClientProperty("annotationController", true);
        }
        
        Class controllerClass = controller.getClass();

        // Get every method into the controller.
        for (Method method : getSwingEventMethods(controllerClass)) {
            SwingEvent annotation = method.getAnnotation(SwingEvent.class);
            if (annotation != null) {
                String[] sources = annotation.sources();
                Class concreteCommand = annotation.command();
                int[] eventIds = annotation.eventIds();

                for (String source : sources) {
                    try {
                        Object component = findComponent(source, view);
                        Receiver receiver = new Receiver(component, controller, method, eventIds);
                        bind(concreteCommand, receiver);
                    } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
                        throw new RuntimeException("Cannot find field " + source + " in view class " + view.getClass().getName());
                    }
                }
            }
        }
    }

    /**
     * Find a Component declared in the View object from the source param. If
     * source param has the same name of the View object, it will be returned.
     * @param source Name of the Component declared in the View object
     * @param view View that contains Swing Components for the GUI
     * @return Object Component
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    private static Object findComponent(String source, Object view)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class viewClass = view.getClass();
        if (source.equals("thisView") || source.equals(viewClass.getSimpleName())) {
            return view;
        }
        Field sourceField = viewClass.getDeclaredField(source);
        sourceField.setAccessible(true);
        return sourceField.get(view);
    }

    /**
     * Bind a Concrete Command object to a Receiver Object.
     * @param concreteCommand Concrete Command that has method named execute
     *        encapsulating actions from Receiver object
     * @param receiver Receiver that contains actions to be called by the
     *        Concrete Command
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static void bind(Class<?> concreteCommand, Receiver receiver)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Class<?>[] types = new Class[]{Receiver.class};
        Constructor<?> constructor = concreteCommand.getConstructor(types);
        Command command = (Command) constructor.newInstance(receiver);
        command.execute();
    }
}