package com.simplepl.astinfoextractor;

import com.simplepl.entity.types.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sad
 */
public class TypeManager {

    private Map<String, Type> types = new HashMap<>();

    public TypeManager() {
        installBuiltinTypes();
    }

    private void installBuiltinTypes() {
        types.put("i64", createTypeObject("i64"));
        types.put("i32", createTypeObject("i32"));
        types.put("i16", createTypeObject("i16"));
        types.put("i8", createTypeObject("i8"));
        types.put("u64", createTypeObject("u64"));
        types.put("u32", createTypeObject("u32"));
        types.put("u16", createTypeObject("u16"));
        types.put("u8", createTypeObject("u8"));
        types.put("f32", createTypeObject("f32"));
        types.put("f64", createTypeObject("f64"));
        types.put("void", createTypeObject("void"));
    }

    public Type getType(String typeName) {
        return types.get(typeName);
    }

    public TypeManager addType(String typeName, Type type) {
        types.put(typeName, type);
        return this;
    }

    private Type createTypeObject(String name) {
        Type type = new Type();
        type.setTypeName(name);
        type.setInternal(new PrimitiveType());
        return type;
    }

    private static class PrimitiveType {

        @Override
        public String toString() {
            return "PrimitiveType";
        }

    }
}
