package com.simplepl.compiler;

import com.simplepl.entity.Context;
import com.simplepl.entity.FunctionInfo;
import com.simplepl.entity.ModuleInfo;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;

public class CompilerTest {

  /*  @Test
    public void testCompileModule() {
        Context context = new Context();
        context.getSrcRoot().addFileSystemRoot(new File(CompilerTest.class.getResource("testdata").getFile()));
        ModuleInfo studentModule = context.getAstManager().getModuleInfo("com.utils.student");
        Assert.assertEquals(1, studentModule.getFunctionList().size());
        FunctionInfo function=studentModule.getFunctionList().get(0);
        Assert.assertEquals("addStudent", function.getName());
        Assert.assertEquals("student", function.getReturnValue().getFullPath());
        Assert.assertEquals(2, studentModule.getImports().size());
        Assert.assertEquals("com.utils.group", studentModule.getImports().get(0).getPath());
        Assert.assertEquals("com.utils.dateutils", studentModule.getImports().get(1).getPath());
        
        Assert.assertEquals(1, function.getArguments().size());
        Assert.assertEquals("name", function.getArguments().get(0).getName());
        Assert.assertEquals("string", function.getArguments().get(0).getType().getFullPath());
        
        StructureInfo studentStructure=studentModule.getStructures().get(0);
        Assert.assertEquals("student", studentStructure.getName());
        Assert.assertEquals("name", studentStructure.getFields().get(0).getName());
        Assert.assertEquals("string", studentStructure.getFields().get(0).getType().getFullPath());
        
        Assert.assertEquals("birthday", studentStructure.getFields().get(1).getName());
        Assert.assertEquals("dateutils.date", studentStructure.getFields().get(1).getType().getFullPath());
        
        Assert.assertEquals("groupField", studentStructure.getFields().get(2).getName());
        Assert.assertEquals("group@", studentStructure.getFields().get(2).getType().getFullPath());
        
        
        ModuleInfo groupModule=context.getAstManager().getModuleInfo("com.utils.group");
        StructureInfo groupStructure=groupModule.getStructures().get(0);
        Assert.assertEquals("group", groupStructure.getName());
        Assert.assertEquals("s1", groupStructure.getFields().get(0).getName());
        Assert.assertEquals("student", groupStructure.getFields().get(0).getType().getFullPath());
        
        Assert.assertTrue("Type for structure student from the different modules are not identical(different instances, but should be singleton)", groupStructure.getFields().get(0).getType()==function.getReturnValue());
    }*/
}