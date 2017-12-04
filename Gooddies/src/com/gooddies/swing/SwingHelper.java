package com.gooddies.swing;

import com.gooddies.swing.event.command.BaseController;
import com.gooddies.wiring.Wiring;

/**
 * @author sad
 */
public class SwingHelper {

    /**
     * try to find controller bean by attaching "Controller" to class name of view's class.<br/>
     * Controller should be WiringComponent and inherited from BaseController
     */
    @SuppressWarnings("unchecked")
    public static void attachController(Object view) {
        String controllerName = view.getClass().getSimpleName() + "Controller";
        Object controller = Wiring.getComponent(controllerName);
        if (controller == null) {
            throw new RuntimeException("Cannot find controller " + controllerName + " for view " + view.getClass().getName());
        }
        if (!(controller instanceof BaseController)) {
            throw new RuntimeException("Controller object should have base class " + BaseController.class.getName());
        }
        attachController(view, (BaseController) controller);
    }

    /**
     * Attach controller to view
     */
    @SuppressWarnings("unchecked")
    public static void attachController(Object view, BaseController controller) {
        controller.setView(view);
    }

    /**
     * Create controller and attach it to view<br/>
     */
    @SuppressWarnings("unchecked")
    public static void attachController(Object view, Class<? extends BaseController> controllerClass) {
        try {
            BaseController controller = controllerClass.newInstance();
            Wiring.get().manualProcessComponent(controller);
            controller.setView(view);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
