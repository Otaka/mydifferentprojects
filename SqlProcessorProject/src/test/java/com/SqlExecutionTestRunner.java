package com;

import com.sqlprocessor.buffers.SqlBuffer;
import com.sqlprocessor.compiler.CompiledSql;
import com.sqlprocessor.compiler.SqlCompiler;
import com.sqlprocessor.compiler.SqlExecutor;
import com.sqlprocessor.utils.StringBuilderWithPadding;
import com.sqlprocessor.utils.StringBuilderWithSeparator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

/**
 * @author sad
 */
public class SqlExecutionTestRunner {

    public void checkTestFilesFromResourceFolder(String pathToResourceFolder) throws IOException, Exception {
        List<String> filesToCheck = getResourcesFromResourcesFolder(pathToResourceFolder, "txt");
        for (String file : filesToCheck) {
            System.out.println("Check file "+file);
            runSqlFromTestFile(file);
        }
        System.out.println("Passed checking " + filesToCheck.size() + " sql test files");
    }

    public void runSqlFromTestFile(String pathToTestFile) throws Exception {
        InputStream inputStream = SqlExecutionTestRunner.class.getResourceAsStream(pathToTestFile);
        if (inputStream == null) {
            throw new IllegalArgumentException("Cannot find test file " + pathToTestFile);
        }

        String sql = null;
        List<SqlBuffer> sqlBuffers = new ArrayList<>();
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        String expectedResult = null;
        String title = null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.toLowerCase().startsWith("table:")) {
                String tableName = line.substring("table:".length()).trim();
                SqlBuffer sqlBuffer = createSqlBuffer(scanner, tableName);
                sqlBuffers.add(sqlBuffer);
            } else if (line.toLowerCase().startsWith("sql:")) {
                sql = readString(scanner);
            } else if (line.toLowerCase().startsWith("expectedresult:")) {
                expectedResult = readString(scanner);
            } else if (line.toLowerCase().startsWith("title:")) {
                title = readString(scanner);
            } else if (line.startsWith("#")) {
                continue;//skip comment
            } else {
                throw new IllegalArgumentException("Do not know how to parse line [" + line + "]");
            }
        }

        if (sql == null) {
            throw new IllegalArgumentException("File [" + pathToTestFile + "] does not contain 'sql:' section");
        }

        SqlBuffer resultBuffer = executeSql(sql, sqlBuffers.toArray(new SqlBuffer[0]));
        if (expectedResult == null) {
            printBuffer(resultBuffer, 30);
        } else {
            String result = bufferToString(resultBuffer);
            try {
                Assert.assertEquals(expectedResult, result);
            } catch (Exception ex) {
                throw new TestException("Error in file [" + pathToTestFile + "]", ex);
            }
        }
    }

    private List<String> getResourcesFromResourcesFolder(String resourcePath, String extension) throws IOException {
        String baseFolderPath = new File("").getAbsolutePath();
        File folder = new File("src/test/java" + resourcePath);
        List<String> foundResources = new ArrayList<>();
        if (!folder.exists()) {
            throw new IllegalArgumentException("Cannot find resource folder [" + folder + "]");
        }

        for (File f : folder.listFiles()) {
            if (f.getName().toLowerCase().endsWith(extension)) {
                String resourceName = f.getAbsolutePath().substring(baseFolderPath.length() + "/src/test/java".length()).replace('\\', '/');
                foundResources.add(resourceName);
            }
        }

        Collections.sort(foundResources, (o1, o2) -> o1.compareToIgnoreCase(o2));

        return foundResources;
    }

    public SqlBuffer executeSql(String sql, SqlBuffer... buffers) throws Exception {
        SqlCompiler compiler = new SqlCompiler();
        CompiledSql compiledSql = compiler.compileSql(sql, buffers);

        SqlExecutor sqlExecutor = compiledSql.getNewSqlExecutor();
        SqlBuffer resultBuffer = sqlExecutor.createOutputSqlBuffer("OutputBuffer");

        boolean testSpeed = false;
        if (testSpeed == false) {
            sqlExecutor.process();
        } else {
            for (int i = 0; i < 100000; i++) {
                sqlExecutor.process();
            }

            long startTime = System.currentTimeMillis();
            int iterationCount = 1000000;
            for (int i = 0; i < iterationCount; i++) {
                sqlExecutor.process();
            }
            long endTime = System.currentTimeMillis();
            System.out.println("Finished running " + iterationCount + " in " + (endTime - startTime) + "ms");
        }
        return resultBuffer;
    }

    public String bufferToString(SqlBuffer buffer) throws Exception {
        StringBuilderWithSeparator sb = new StringBuilderWithSeparator(",");
        for (String field : (List<String>) buffer.getFields()) {
            sb.append(field).newEntry();
        }
        sb.skipNewEntry();
        sb.append("\n");
        for (Object data : buffer.getData()) {
            for (String field : (List<String>) buffer.getFields()) {
                Field f = buffer.getField(field);
                f.setAccessible(true);
                Object result = f.get(data);
                if (result == null) {
                    sb.append("null").newEntry();
                } else {
                    sb.append(result.toString()).newEntry();
                }
            }
            sb.skipNewEntry();

            sb.append("\n");
        }

        return StringUtils.stripEnd(sb.toString(), " \t\n\r");
    }

    public String printBuffer(SqlBuffer buffer, int columnWidth) throws Exception {
        StringBuilderWithPadding sb = new StringBuilderWithPadding();
        sb.print("|");
        for (String field : (List<String>) buffer.getFields()) {
            sb.print(StringUtils.center("", columnWidth, '-'));
            sb.print("|");
        }
        sb.println("");

        sb.print("|");
        for (String field : (List<String>) buffer.getFields()) {
            sb.print(StringUtils.center(field, columnWidth, ' '));
            sb.print("|");
        }
        sb.println("");

        sb.print("|");
        for (String field : (List<String>) buffer.getFields()) {
            sb.print(StringUtils.center("", columnWidth, '-'));
            sb.print("|");
        }
        sb.println("");

        for (Object data : buffer.getData()) {
            sb.print("|");
            for (String field : (List<String>) buffer.getFields()) {
                Field f = buffer.getField(field);
                f.setAccessible(true);
                Object result = f.get(data);
                sb.print(StringUtils.center(result.toString(), columnWidth, ' '));
                sb.print("|");
            }
            sb.println("");
        }

        sb.print("|");
        for (String field : (List<String>) buffer.getFields()) {
            sb.print(StringUtils.center("", columnWidth, '-'));
            sb.print("|");
        }
        sb.println();
        System.out.println(sb.toString());
        return sb.toString();
    }

    private String readString(Scanner scanner) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        while (scanner.hasNextLine()) {
            String line = StringUtils.stripEnd(scanner.nextLine(), " \t");
            if (line.isEmpty()) {
                break;
            }
            if (first == false) {
                sb.append("\n");
            }
            sb.append(line);
            first = false;
        }

        return sb.toString();
    }

    private SqlBuffer createSqlBuffer(Scanner scanner, String tableName) throws Exception {
        String headerLine = scanner.nextLine();
        String[] headers = headerLine.split(",");
        Class clazz = generateClass(headers, tableName);
        Method[] setters = new Method[headers.length];
        Class[] columnTypes = new Class[headers.length];
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i].trim();
            String h = header.substring(0, header.indexOf('#'));
            String typeString = header.substring(header.indexOf('#') + 1);
            setters[i] = clazz.getDeclaredMethod("set" + StringUtils.capitalize(h), new Class[]{getClassForTypeString(typeString)});
            columnTypes[i] = getClassForTypeString(typeString);
        }

        List data = new ArrayList();
        while (scanner.hasNextLine()) {
            String string = scanner.nextLine().trim();
            if (string.isEmpty()) {
                break;
            }

            Object object = clazz.newInstance();
            String[] columnParts = string.split(",");
            if (columnParts.length != headers.length) {
                throw new IllegalArgumentException("Error in table [" + tableName + "]. Line has " + columnParts.length + " but header contains " + headers.length + ". Line [" + string + "]");
            }

            for (int i = 0; i < headers.length; i++) {
                setters[i].invoke(object, parseValue(columnParts[i].trim(), columnTypes[i]));
            }

            data.add(object);
        }

        List<String> fields = new ArrayList<>();
        for (String header : headers) {
            fields.add(header.substring(0, header.indexOf('#')).trim());
        }

        SqlBuffer sqlBuffer = new SqlBuffer(tableName, clazz, fields);
        sqlBuffer.setData(data);
        return sqlBuffer;
    }

    private static AtomicInteger index = new AtomicInteger(0);

    private Object parseValue(String strValue, Class clazz) {
        if (clazz == int.class) {
            return Integer.parseInt(strValue);
        }
        if (clazz == long.class) {
            return Long.parseLong(strValue);
        }
        if (clazz == double.class) {
            return Double.parseDouble(strValue);
        }
        if (clazz == float.class) {
            return Float.parseFloat(strValue);
        }
        if (clazz == boolean.class) {
            return Boolean.parseBoolean(strValue);
        }
        if (clazz == String.class) {
            return strValue;
        }
        throw new IllegalArgumentException("Do not know how to parse value [" + strValue + "] as [" + clazz.getName() + "]");
    }

    private Class generateClass(String[] headers, String tableName) {
        String packagePath = "com.test.sqlgenerator.table";
        String className = tableName + index.incrementAndGet();
        StringBuilderWithPadding sb = new StringBuilderWithPadding();
        sb.println("package " + packagePath + ";");
        sb.println("public class " + className + " {");
        sb.incLevel();

        for (String header : headers) {
            header = header.trim();
            if (!header.contains("#")) {
                throw new IllegalArgumentException("Field [" + header + "] in table [" + tableName + "] should contain type (Example name#string)");
            }

            String fieldName = header.substring(0, header.indexOf('#')).trim();
            String typeString = header.substring(header.indexOf('#') + 1).trim();
            Class type = getClassForTypeString(typeString);
            sb.println("public " + type.getName() + " " + fieldName + ";");
            sb.println("public " + type.getName() + " get" + StringUtils.capitalize(fieldName) + "() {");
            sb.incLevel();
            sb.println("return " + fieldName + ";");
            sb.decLevel();
            sb.println("}");

            sb.println("public void set" + StringUtils.capitalize(fieldName) + "(" + type.getName() + " " + fieldName + ") {");
            sb.incLevel();
            sb.println("this." + fieldName + "=" + fieldName + ";");
            sb.decLevel();
            sb.println("}");
        }

        sb.decLevel();
        sb.println("}");

        return SqlCompiler.compileClass(packagePath, className, sb.toString());
    }

    private Class getClassForTypeString(String type) {
        if (type.equalsIgnoreCase("int")) {
            return int.class;
        }

        if (type.equalsIgnoreCase("long")) {
            return long.class;
        }

        if (type.equalsIgnoreCase("double")) {
            return double.class;
        }

        if (type.equalsIgnoreCase("String")) {
            return String.class;
        }

        if (type.equalsIgnoreCase("boolean")) {
            return String.class;
        }

        throw new IllegalArgumentException("Cannot parse type [" + type + "]");
    }

    public static class TestException extends RuntimeException {

        public TestException(String message, Throwable cause) {
            super(message, cause);
        }

    }

}
