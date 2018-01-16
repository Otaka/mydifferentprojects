package com.settings.editor.builder;

import com.settings.editor.components.annotations.PropertyNumberDouble;
import com.settings.editor.components.annotations.PropertyInt;
import com.settings.editor.components.annotations.PropertyText;
import com.settings.editor.components.PropertyColorBuilder;
import com.settings.editor.components.PropertyFileBuilder;
import com.settings.editor.components.PropertyIntBuilder;
import com.settings.editor.components.PropertyTextBuilder;
import com.settings.editor.components.annotations.PropertyColor;
import com.settings.editor.components.annotations.PropertyFile;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;

/**
 * @author sad
 */
public class SettingsPanelBuilder {

    private Map<String, CategoryProperties> categoryProperties = new HashMap<>();

    private final List<PropertyHolder> properties = new ArrayList<PropertyHolder>();

    public void setCategoryProperties(Map<String, CategoryProperties> categoryProperties) {
        this.categoryProperties = categoryProperties;
    }

    private Annotation getAnnotation(Field field, Class... annotations) {
        for (Class a : annotations) {
            if (field.getAnnotation(a) != null) {
                return field.getAnnotation(a);
            }
        }

        return null;
    }

    private List<PropertyHolder> extractProperties(Object obj) {
        Class clazz = obj.getClass();
        List<PropertyHolder> tProperties = new ArrayList<>();
        while (clazz != null) {
            for (Field f : clazz.getDeclaredFields()) {
                Annotation annotation = getAnnotation(f, PropertyInt.class, PropertyFile.class, PropertyColor.class, PropertyNumberDouble.class, PropertyText.class);
                if (annotation != null) {
                    Method getter = getGetter(obj, f);
                    Method setter = getSetter(obj, f);
                    String category = getCategory(annotation, f, obj);
                    String name = getName(annotation, f, obj);
                    PropertyHolder propertyHolder = new PropertyHolder(f, setter, getter, obj, annotation, category, name);
                    tProperties.add(propertyHolder);
                }
            }

            clazz = clazz.getSuperclass();
        }

        return tProperties;
    }

    private String getCategory(Annotation annotation, Field f, Object obj) {
        try {
            return (String) annotation.getClass().getMethod("category").invoke(annotation);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private String getName(Annotation annotation, Field f, Object obj) {
        try {
            return (String) annotation.getClass().getMethod("name").invoke(annotation);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private Method getSetter(Object obj, Field field) {
        String name = field.getName().toLowerCase();
        String[] candidates = new String[]{"set" + name};
        for (String candidate : candidates) {
            try {
                Method[] methods = obj.getClass().getMethods();
                for (Method m : methods) {
                    if (m.getName().toLowerCase().equals(candidate)) {
                        return m;
                    }
                }
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private Method getGetter(Object obj, Field field) {
        String name = field.getName().toLowerCase();
        String[] candidates = new String[]{"get" + name, "is" + name};
        for (String candidate : candidates) {
            try {
                Method[] methods = obj.getClass().getMethods();
                for (Method m : methods) {
                    if (m.getName().toLowerCase().equals(candidate)) {
                        return m;
                    }
                }
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    private JPanel createEmptyPanel() {
        JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(100, 100));
        return panel;
    }

    public JComponent build(Object object) {
        List<PropertyHolder> properties = extractProperties(object);
        if (properties.isEmpty()) {
            return createEmptyPanel();
        }

        Map<String, List<PropertyHolder>> propertiesByCategory = new HashMap<String, List<PropertyHolder>>();
        for (PropertyHolder property : properties) {
            List<PropertyHolder> propertiesInCategory = propertiesByCategory.get(property.getCategory());
            if (propertiesInCategory == null) {
                propertiesInCategory = new ArrayList<>();
                propertiesByCategory.put(property.getCategory(), propertiesInCategory);
            }
            propertiesInCategory.add(property);
        }

        if (propertiesByCategory.keySet().size() == 1) {
            JComponent panel = createPanelByCategory(properties);
            return panel;
        }

        JPanel wrapperPanel = createEmptyPanel();
        wrapperPanel.setLayout(new BorderLayout(0, 0));
        JTabbedPane tabbedPane = new JTabbedPane();
        wrapperPanel.add(tabbedPane);
        for (String category : propertiesByCategory.keySet()) {
            List<PropertyHolder> props = propertiesByCategory.get(category);
            JComponent panel = createPanelByCategory(props);
            tabbedPane.addTab(category, panel);
        }

        return wrapperPanel;
    }

    private JComponent createPanelByCategory(List<PropertyHolder> properties) {
        JScrollPane scrollPane = new JScrollPane();
        JPanel wr = new JPanel();
        wr.setLayout(new BorderLayout(20, 20));
        JPanel panel = new JPanel(new MigLayout("insets 5 5 0 0"));
        for (PropertyHolder holder : properties) {
            createComponent(panel, holder);
        }
        wr.add(panel);
        scrollPane.getViewport().add(wr);
        return scrollPane;
    }

    private JComponent createComponent(JPanel panel, PropertyHolder property) {
        if (property.getAnnotation() instanceof PropertyInt) {
            return new PropertyIntBuilder().createComponent(property, panel);
        }
        if (property.getAnnotation() instanceof PropertyText) {
            return new PropertyTextBuilder().createComponent(property, panel);
        }
        if (property.getAnnotation() instanceof PropertyColor) {
            return new PropertyColorBuilder().createComponent(property, panel);
        }
        if (property.getAnnotation() instanceof PropertyFile) {
            return new PropertyFileBuilder().createComponent(property, panel);
        }

        throw new RuntimeException("PropertyComponentBuilder is still not implemented for annotation " + property.getAnnotation().annotationType().getName());
    }
}
