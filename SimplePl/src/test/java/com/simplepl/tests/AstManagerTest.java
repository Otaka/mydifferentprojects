package com.simplepl.tests;

import com.simplepl.BaseTest;
import com.simplepl.entity.Context;
import com.simplepl.entity.ModuleInfo;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author sad
 */
public class AstManagerTest extends BaseTest {

    @Test
    public void testModuleTypeCollection() throws IOException {
        Context context = new Context();
        context.getSrcRoot().addFileSystemRoot(new File("./src/test/java/com/simplepl/tests"));
        ModuleInfo moduleInfo = context.getAstManager().getModuleInfo("com.test.moduleTypeCollectSourceFile");
        ModuleInfo utilsModuleInfo = context.getAstManager().getModuleInfo("com.test.utils");

        Assert.assertEquals("com.test.moduleTypeCollectSourceFile", moduleInfo.getModule());
        Assert.assertEquals(1, moduleInfo.getFunctionList().size());
        Assert.assertEquals("main", moduleInfo.getFunctionList().get(0).getName());
        Assert.assertEquals("com.test.moduleTypeCollectSourceFile.int", moduleInfo.getFunctionList().get(0).getReturnType().getType().getTypeName());

        Assert.assertEquals("com.test.utils", utilsModuleInfo.getModule());
        Assert.assertEquals(1, utilsModuleInfo.getFunctionList().size());
        Assert.assertEquals(1, utilsModuleInfo.getStructuresList().size());
        Assert.assertEquals("UtilsStructure", utilsModuleInfo.getStructuresList().get(0).getName());
    }
}
