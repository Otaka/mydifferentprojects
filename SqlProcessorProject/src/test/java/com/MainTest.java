package com;

import com.sqlprocessor.compiler.CompiledSql;
import com.sqlprocessor.compiler.SqlCompiler;
import com.sqlprocessor.compiler.exception.CannotCompileClassException;
import com.testentities.TestDataFactory;
import java.lang.reflect.Method;
import net.openhft.compiler.CompilerUtils;
import net.sf.jsqlparser.JSQLParserException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author sad
 */
public class MainTest extends SqlExecutionTestRunner {

    @Test
    public void testCompileSql() throws JSQLParserException, Exception {
        //executeSql("select s.id as studentId, s.name as studentName, h.name as houseName from students s, houses h where s.houseId=h.id  ORDER BY s.name desc",
        //        TestDataFactory.createHouseBuffer(),
        //        TestDataFactory.createStudentBuffer());
    }

    @Test
    public void testCompilerCanUseGeneratedClasses() throws Exception {
        String myClassASourceCode = "package com.myclasses;\npublic class MyClassA{\nprivate String a=\"hello from generated class\";\npublic String getA(){return a;}\n}";
        compileClass("com.myclasses", "MyClassA", myClassASourceCode);
        String userOfClassASourceCode = "package com.myclasses;\npublic class MyTestClass{\npublic static String getDataFromMyClassA(){\nMyClassA a=new MyClassA();\n return a.getA();}\n}";
        Class userOfClassAClass = compileClass("com.myclasses", "MyTestClass", userOfClassASourceCode);
        Method method = userOfClassAClass.getDeclaredMethod("getDataFromMyClassA", new Class[0]);
        method.setAccessible(true);
        String result = (String) method.invoke(null, new Object[0]);
        Assert.assertEquals("hello from generated class", result);
    }

    private Class compileClass(String packagePath, String className, String sourceCode) {
        try {
            Class resultClazz = CompilerUtils.CACHED_COMPILER.loadFromJava(packagePath + "." + className, sourceCode);
            return resultClazz;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new CannotCompileClassException("Internal exception. Cannot compile CombinedWorkingTable class [\n" + sourceCode + "\n]", ex);
        }
    }
}
