package com.simplepl.astinfoextractor;

import com.simplepl.entity.Context;
import com.simplepl.entity.types.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class ModuleTypeFinder {

    private Context context;
    private List<String> listOfImportedPackagesForModule = new ArrayList<>();

    public ModuleTypeFinder(Context context) {
        this.context = context;
    }

    public void addImport(String importPath) {
        listOfImportedPackagesForModule.add(importPath);
    }

    public Type searchTypeForModule(String typeName) {
        Type type = context.getTypeManager().getType(typeName);
        if (type == null) {
            for (String importedPackage : listOfImportedPackagesForModule) {
                type = context.getTypeManager().getType(importedPackage+"."+typeName);
                if(type!=null){
                    break;
                }
            }
        }

        return type;
    }
}
