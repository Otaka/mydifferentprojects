package com.simplepl.tests;

import com.simplepl.BaseTest;
import com.simplepl.entity.Context;
import com.simplepl.entity.ModuleInfo;
import java.io.File;
import java.io.IOException;
import org.junit.Test;

/**
 * @author sad
 */
public class AstManagerTest extends BaseTest {

    @Test
    public void testModuleTypeCollection() throws IOException {
        Context context = new Context();
        context.getSrcRoot().addFileSystemRoot(new File("./src/test/java/com/simplepl/tests"));
        ModuleInfo moduleInfo= context.getAstManager().getModuleInfo("com.test.moduleTypeCollectSourceFile");
        moduleInfo=null;
    }
}
