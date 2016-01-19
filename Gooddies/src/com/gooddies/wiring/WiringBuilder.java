package com.gooddies.wiring;

import com.gooddies.exceptions.WiringException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class WiringBuilder {

    private final List<String> scanPackages = new ArrayList<String>();
    private Wiring wiring;

    WiringBuilder() {
        if (Wiring.isInitialized()) {
            throw new WiringException("Wiring has been already initialized");
        }
        wiring = new Wiring();
    }

    public WiringBuilder addScanPackage(String scanPackage) {
        scanPackages.add(scanPackage);
        return this;
    }

    public WiringBuilder addPrimitiveComponent(String name, Object value) {
        wiring.addPrimitiveComponent(name, value);
        return this;
    }

    public WiringBuilder processJsonConfig(File file) {
        JsonPrimitiveComponentInitializer initializer = new JsonPrimitiveComponentInitializer();
        initializer.process(wiring, file);
        return this;
    }

    public WiringBuilder addOverride(String name, Object value) {
        wiring.addOverride(name, value);
        return this;
    }

    public Wiring build() {
        for (String path : scanPackages) {
            wiring.process(path);
        }

        wiring.build();
        return wiring;
    }

    public Wiring defaultInit(Class mainClass) {
        addScanPackage(mainClass.getPackage().getName());
        return build();
    }
}
