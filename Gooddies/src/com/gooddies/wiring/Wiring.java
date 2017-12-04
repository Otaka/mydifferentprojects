package com.gooddies.wiring;

import com.gooddies.exceptions.WiringException;
import com.gooddies.reflection.ReflectionUtils;
import com.gooddies.utils.AnyToAnyConverter;
import com.gooddies.wiring.annotations.PostWiring;
import com.gooddies.wiring.annotations.Wire;
import com.gooddies.wiring.annotations.WiringComponent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

/**
 * @author sad
 */
@SuppressWarnings("unchecked")
public class Wiring {

    private static Wiring instance;
    private Map<String, WiringComponentHolder> components = new HashMap<String, WiringComponentHolder>(100);
    private Map<String, WiringComponentHolder> overrideComponents = new HashMap<String, WiringComponentHolder>(100);
    private Map<Class, ClassInfo> classInfoCache = new HashMap<Class, ClassInfo>();
    private Map<String, Object> primitiveComponents = new HashMap<String, Object>();

    Wiring() {
        instance = this;
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static Wiring get() {
        if (instance == null) {
            throw new NullPointerException("Wiring is not initialized properly. Use Wiring.init().build()");
        }

        return instance;
    }

    public static WiringBuilder init() {
        if (instance != null) {
            throw new WiringException("Wiring has been already initialized");
        }

        return new WiringBuilder();
    }

    private void addWiringComponent(String name, Class clazz, Object value, boolean isOverriden, boolean singleton, boolean isDefault, boolean lazy) {
        Map<String, WiringComponentHolder> componentsMap = components;
        if (isOverriden) {
            componentsMap = overrideComponents;
        }
        if (value == null && lazy == false) {
            throw new WiringException(String.format("Cannot add null WiringComponent with name [%s] to context", name));
        }

        if (primitiveComponents.containsKey(name)) {
            throwComponentAlreadyExist(name, true);
        }

        if (componentsMap.containsKey(name)) {
            WiringComponentHolder holder = componentsMap.get(name);
            if (!holder.isDefault()) {
                throwComponentAlreadyExist(name, false);
            }
        }

        WiringComponentHolder holder = new WiringComponentHolder(name, clazz, value, singleton, isDefault, lazy);
        componentsMap.put(name, holder);
    }

    private void throwComponentAlreadyExist(String name, boolean isInPrimitive) {
        if (isInPrimitive) {
            throw new WiringException(String.format("WiringComponent with name [%s] already exists in context(Exists as primitive object). Duplication is not allowed!", name));
        } else {
            throw new WiringException(String.format("WiringComponent with name [%s] already exists in context. Duplication is not allowed! Are you forget to add 'default'?", name));
        }
    }

    private String getComponentName(Class clazz, WiringComponent annotation) {
        String name = annotation.name();
        if (WiringComponent.defaultName.equals(name)) {
            name = clazz.getSimpleName();
        }

        return name;
    }

    public void addPrimitiveComponent(String componentName, Object component) {
        if (primitiveComponents.containsKey(componentName)) {
            throw new WiringException("Cannot add primitive component to Wiring context, because it already exist [" + componentName + "]");
        }

        if (component == null) {
            throw new WiringException("Cannot add null primitive component to Wiring context, [" + componentName + "]");
        }

        primitiveComponents.put(componentName, component);
    }

    public void addClass(Class clazz) {
        WiringComponent annotation = (WiringComponent) clazz.getAnnotation(WiringComponent.class);
        if (annotation == null) {
            return;
        }

        String className = getComponentName(clazz, annotation);
        Object value;
        if (annotation.singleton()) {
            if (annotation.lazy()) {
                value = null;
            } else {
                value = instantiate(clazz);
            }
        } else {
            value = true;
        }

        addWiringComponent(className, clazz, value, false, annotation.singleton(), annotation.isDefault(), annotation.lazy());
    }

    public void addOverride(String name, Object value) {
        addWiringComponent(name, value.getClass(), value, true, true, false, false);
    }

    void process(String packagePath) {
        List<Class> classes = ReflectionUtils.listClassesInPackage(packagePath);
        for (Class clazz : classes) {
            addClass(clazz);
        }
    }

    public List<WiringComponentHolder> getAllWiringComponents() {
        List<WiringComponentHolder> tComponents = new ArrayList<WiringComponentHolder>();
        for (WiringComponentHolder component : components.values()) {
            if (!overrideComponents.containsKey(component.getName())) {
                tComponents.add(component);
            }
        }

        for (WiringComponentHolder component : overrideComponents.values()) {
            tComponents.add(component);
        }

        return tComponents;
    }

    public <T> T getWiringComponent(Class<T> clazz) {
        return (T) getWiringComponent(clazz.getSimpleName());
    }

    public static <T> T getComponent(Class<T> clazz) {
        return (T) get().getWiringComponent(clazz.getSimpleName());
    }

    public Object getWiringComponent(String name) {
        return getWiringComponent(name, new WiringCreateContext());
    }

    public static Object getComponent(String name) {
        return get().getWiringComponent(name, new WiringCreateContext());
    }

    private Object getWiringComponent(String name, WiringCreateContext context) {
        WiringComponentHolder comp = overrideComponents.get(name);
        if (comp != null) {
            return comp.getObject();
        }

        comp = components.get(name);
        if (comp != null) {
            if (comp.isSingleton()) {
                if (comp.getObject() == null && comp.isLazy()) {
                    Object value = createNewWiringComponentInstance(comp.getClazz(), context);
                    comp.setObject(value);
                    return value;
                } else {
                    return comp.getObject();
                }
            } else {
                if (context == null) {
                    context = new WiringCreateContext();
                }

                Class clazz = comp.getClazz();
                //if (!context.add(clazz)) {
                //    throw new WiringException(formatCyclicDependencyMessage(context, clazz));
                //}

                return createNewWiringComponentInstance(clazz, context);
            }
        }
        Object primitive = primitiveComponents.get(name);
        if (primitive != null) {
            return primitive;
        }

        throw new WiringException("Threre is no such bean [" + name + "]");
    }

    private String getClassName(Class clazz) {
        String name = clazz.getSimpleName();
        if (name == null || name.isEmpty()) {
            name = clazz.getName();
        }

        return name;
    }

    private String formatCyclicDependencyMessage(WiringCreateContext context, Class lastClass) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("[");
        List<Class> classes = new ArrayList<Class>();
        classes.addAll(context.getCreationComponentStack());
        classes.add(lastClass);
        for (Class clazz : classes) {
            if (!first) {
                sb.append(", ");
            }

            sb.append(getClassName(clazz));
            first = false;
        }

        sb.append("]");
        return "Class hierarchy contains cyclic dependency " + sb.toString() + " with class " + getClassName(lastClass);
    }

    private Object createNewWiringComponentInstance(Class clazz, WiringCreateContext context) {
        return newInstance(clazz, context);
    }

    void build() {
        List<WiringComponentHolder> tComponents = getAllWiringComponents();
        for (WiringComponentHolder holder : tComponents) {
            if (holder.isSingleton()) {
                if (!holder.isLazy()) {
                    populateComponent(holder.getObject(), getClassInfo(holder.getClazz()), null);
                }
            }
        }

        for (WiringComponentHolder holder : tComponents) {
            if (holder.isSingleton() && !holder.isLazy()) {
                executePostMethods(holder.getObject(), getClassInfo(holder.getClazz()));
            }
        }
    }

    public void manualProcessComponent(Object component) {
        manualProcessComponent(component, null);
    }

    private void manualProcessComponent(Object component, WiringCreateContext context) {
        ClassInfo classInfo = getClassInfo(component.getClass());
        populateComponent(component, classInfo, context);
        executePostMethods(component, classInfo);
    }

    private ClassInfo getClassInfo(Class clazz) {
        ClassInfo classInfo = classInfoCache.get(clazz);
        if (classInfo == null) {
            classInfo = createClassInfo(clazz);
            classInfoCache.put(clazz, classInfo);
        }

        return classInfo;
    }

    private ClassInfo createClassInfo(Class clazz) {
        List<Field> fieldsList = new ArrayList<Field>();
        List<Wire> annotationsList = new ArrayList<Wire>();
        List<Method> methodsList = new ArrayList<Method>();
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new WiringException("Class " + getClassName(clazz) + " cannot be component, because it is abstract");
        }

        Constructor constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new WiringException("Class " + getClassName(clazz) + " does not have default constructor. Please add it.");
        }

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            Method[] methods = clazz.getDeclaredMethods();
            for (Field f : fields) {
                Wire annotation = f.getAnnotation(Wire.class);
                if (annotation != null) {
                    f.setAccessible(true);
                    annotationsList.add(annotation);
                    fieldsList.add(f);
                }
            }

            for (Method m : methods) {
                Object annotation = m.getAnnotation(PostWiring.class);
                if (annotation == null) {
                    annotation = m.getAnnotation(PostConstruct.class);
                }
                if (annotation != null) {
                    if (m.getParameterTypes().length != 0) {
                        throw new WiringException(String.format("Cannot execute PostWiring method [%s] in class [%s] because this method should not have parameters, but it has", m.getName(), clazz.getName()));
                    }
                    m.setAccessible(true);
                    methodsList.add(m);
                }
            }

            clazz = clazz.getSuperclass();
        }

        Collections.reverse(methodsList);
        return new ClassInfo(methodsList.toArray(new Method[methodsList.size()]), fieldsList.toArray(new Field[fieldsList.size()]), annotationsList.toArray(new Wire[0]), constructor);
    }

    private void executePostMethods(Object component, ClassInfo classInfo) {
        Method[] methods = classInfo.getPostMethods();
        for (Method m : methods) {
            executePostMethod(component, m);
        }
    }

    private void populateComponent(Object component, ClassInfo classInfo, WiringCreateContext context) {
        Field[] fields = classInfo.getWiredFields();
        Wire[] annotations = classInfo.getAnnotations();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            Wire annotation = annotations[i];
            populateField(component, f, annotation, context);
        }
    }

    private void executePostMethod(Object component, Method m) {
        try {
            m.invoke(component);
        } catch (Exception ex) {
            throw new WiringException(ex);
        }
    }

    private boolean isPrimitiveComponent(String name) {
        return primitiveComponents.containsKey(name);
    }

    private void populateField(Object component, Field field, Wire annotation, WiringCreateContext context) {
        String componentName;
        if (Wire.defaultName.equals(annotation.value())) {
            componentName = field.getType().getSimpleName();
        } else {
            componentName = annotation.value();
        }

        Object componentToInject = getWiringComponent(componentName, context);
        if (componentToInject == null) {
            throw new WiringException(String.format("Class [%s] field [%s] cannot find inject component with name [%s] in context",
                    component.getClass().getName(), field.getName(), componentName));
        }

        if (isPrimitiveComponent(componentName)) {
            processInsertPrimitiveComponent(component, componentToInject, field, componentName);
        } else {
            try {
                field.set(component, componentToInject);
            } catch (ClassCastException ex) {
                throw new WiringException(String.format("Wiring class cast exception. Class [%s] field [%s] of type[%s] cannot inject component with name [%s] of type [%s]",
                        component.getClass().getName(), field.getName(), field.getType().getName(), componentName, componentToInject.getClass().getName()));
            } catch (IllegalAccessException ex) {
                throw new WiringException(ex);
            }
        }
    }

    private boolean isChild(Class child, Class parent) {
        try {
            child.asSubclass(parent);
            return true;
        } catch (ClassCastException ex) {
        }
        return false;
    }

    private void processInsertPrimitiveComponent(Object component, Object componentToInject, Field field, String componentName) {
        Object newValue = componentToInject;
        if (!isChild(componentToInject.getClass(), field.getType())) {
            newValue = AnyToAnyConverter.convert(componentToInject.getClass(), field.getType(), componentToInject);
        }

        try {
            field.set(component, newValue);
        } catch (ClassCastException ex) {
            throw new WiringException(String.format("Wiring class cast exception. Class [%s] field [%s] of type[%s] cannot inject component with name [%s] of type [%s]",
                    component.getClass().getName(), field.getName(), field.getType().getName(), componentName, componentToInject.getClass().getName()));
        } catch (IllegalAccessException ex) {
            throw new WiringException(ex);
        }
    }

    private Object instantiate(Class clazz) {
        try {
            ClassInfo info = getClassInfo(clazz);
            if (info.getConstructor() == null) {
                return clazz.newInstance();
            } else {
                return info.getConstructor().newInstance();
            }
        } catch (Exception ex) {
            throw new WiringException("Cannot instantiate class " + clazz.getName(), ex);
        }
    }

    private Object newInstance(Class clazz, WiringCreateContext context) {
        try {
            Object obj = instantiate(clazz);
            manualProcessComponent(obj, context);
            return obj;
        } catch (Exception ex) {
            throw new WiringException(ex);
        }
    }
}
