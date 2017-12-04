package com.sqlprocessor.compiler;

import com.sqlprocessor.table.SqlField;
import com.sqlprocessor.table.TableManager;
import com.sqlprocessor.utils.RuntimeUtils;
import java.lang.reflect.Field;
import java.util.Stack;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author sad
 */
public class ExpressionExecutor {

    public static class ExpressionExecutorResult {

        public String expressionExecSourceCode;
        public Class resultType;
    }

    public ExpressionExecutorResult executeExpression(Expression expression, TableManager tableManager, SourceCode sourceCodeGenerator, String rowVariableName) {
        Stack<Class> typeStack = new Stack<>();
        Stack<String> sourceCodeStack = new Stack<>();
        expression.accept(new ExpressionDeParser() {
            @Override
            public void visit(LikeExpression likeExpression) {
                super.visit(likeExpression);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                if (!(isString(leftType) && isString(rightType))) {
                    throw new IllegalArgumentException("Like expression can have only String LIKE String arguments, but found " + leftType.getSimpleName() + " LIKE " + rightType.getSimpleName());
                }

                String pattern = RuntimeUtils.convertSqlLikeToRegex(rightCode);
                String patternVariable = "pattern_" + sourceCodeGenerator.getNextFreeId();
                String initPatternLine = "java.util.regex.Pattern " + patternVariable + "= java.util.regex.Pattern.compile(\"" + StringEscapeUtils.escapeJava(pattern) + "\"";
                if (likeExpression.isCaseInsensitive()) {
                    initPatternLine += ",java.util.regex.Pattern.CASE_INSENSITIVE";
                }

                initPatternLine = ");\n";
                sourceCodeGenerator.addInitializationSourceCode(initPatternLine);
                sourceCodeStack.push(patternVariable + ".matcher(" + leftCode + ").matches()");
                typeStack.push(boolean.class);
            }

            @Override
            public void visit(Addition addition) {
                super.visit(addition);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                String sourceCode;
                Class resultType;
                if (isInt(leftType)) {
                    if (isInt(rightType)) {
                        resultType = int.class;
                        sourceCode = leftCode + "+" + rightCode;
                    } else if (isLong(rightType)) {
                        resultType = long.class;
                        sourceCode = leftCode + "+" + rightCode;
                    } else if (isDouble(rightType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "+" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot add integer to " + rightType.getSimpleName());
                    }
                } else if (isLong(leftType)) {
                    if (isInt(rightType) || isLong(rightType)) {
                        resultType = long.class;
                        sourceCode = leftCode + "+" + rightCode;
                    } else if (isDouble(rightType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "+" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot add long to " + rightType.getSimpleName());
                    }
                } else if (isDouble(leftType)) {
                    if (isInt(rightType) || isLong(rightType) || isDouble(leftType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "+" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot add double to " + rightType.getSimpleName());
                    }
                } else if (isString(leftType)) {
                    if (isInt(rightType) || isLong(rightType) || isDouble(leftType) || isString(leftType) || isBoolean(leftType)) {
                        resultType = String.class;
                        sourceCode = leftCode + ".concat(String.valueOf(" + rightCode + "))";
                    } else {
                        throw new IllegalArgumentException("Cannot add string to " + rightType.getSimpleName());
                    }
                } else if (isBoolean(leftType)) {
                    throw new IllegalArgumentException("Cannot add boolean to " + rightType.getSimpleName());
                } else {
                    throw new IllegalArgumentException("Cannot add " + leftType.getSimpleName() + " to " + rightType.getSimpleName());
                }

                sourceCodeStack.push(sourceCode);
                typeStack.push(resultType);
            }

            @Override
            public void visit(Subtraction subtraction) {
                super.visit(subtraction);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                String sourceCode;
                Class resultType;
                if (isInt(leftType)) {
                    if (isInt(rightType)) {
                        resultType = int.class;
                        sourceCode = leftCode + "-" + rightCode;
                    } else if (isLong(rightType)) {
                        resultType = long.class;
                        sourceCode = leftCode + "-" + rightCode;
                    } else if (isDouble(rightType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "-" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot subtract " + rightType.getSimpleName() + " from integer");
                    }
                } else if (isLong(leftType)) {
                    if (isInt(rightType) || isLong(rightType)) {
                        resultType = long.class;
                        sourceCode = leftCode + "-" + rightCode;
                    } else if (isDouble(rightType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "-" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot subtract " + rightType.getSimpleName() + " from long");
                    }
                } else if (isDouble(leftType)) {
                    if (isInt(rightType) || isLong(rightType) || isDouble(leftType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "-" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot subtract " + rightType.getSimpleName() + " from double");
                    }
                } else if (isString(leftType)) {
                    throw new IllegalArgumentException("Cannot subtract " + rightType.getSimpleName() + " from string");
                } else if (isBoolean(leftType)) {
                    throw new IllegalArgumentException("Cannot subtract " + rightType.getSimpleName() + " from boolean");
                } else {
                    throw new IllegalArgumentException("Cannot subtract " + rightType.getSimpleName() + " from " + leftType.getSimpleName());
                }

                sourceCodeStack.push(sourceCode);
                typeStack.push(resultType);
            }

            @Override
            public void visit(Division division) {
                super.visit(division);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                String sourceCode;
                Class resultType;

                if (isInt(leftType)) {
                    if (isInt(rightType)) {
                        resultType = int.class;
                        sourceCode = leftCode + "/" + rightCode;
                    } else if (isLong(rightType)) {
                        resultType = long.class;
                        sourceCode = leftCode + "/" + rightCode;
                    } else if (isDouble(rightType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "/" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot make integer/" + rightType.getSimpleName());
                    }
                } else if (isLong(leftType)) {
                    if (isInt(rightType) || isLong(rightType)) {
                        resultType = long.class;
                        sourceCode = leftCode + "/" + rightCode;
                    } else if (isDouble(rightType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "/" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot make long/" + rightType.getSimpleName());
                    }
                } else if (isDouble(leftType)) {
                    if (isInt(rightType) || isLong(rightType) || isDouble(leftType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "/" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot make double/" + rightType.getSimpleName());
                    }
                } else if (isString(leftType)) {
                    throw new IllegalArgumentException("Cannot make string/" + rightType.getSimpleName());
                } else if (isBoolean(leftType)) {
                    throw new IllegalArgumentException("Cannot make boolean/" + rightType.getSimpleName());
                } else {
                    throw new IllegalArgumentException("Cannot make " + leftType.getSimpleName() + "/" + rightType.getSimpleName());
                }

                sourceCodeStack.push(sourceCode);
                typeStack.push(resultType);
            }

            @Override
            public void visit(Multiplication multiplication) {
                super.visit(multiplication);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                String sourceCode;
                Class resultType;
                if (isInt(leftType)) {
                    if (isInt(rightType)) {
                        resultType = int.class;
                        sourceCode = leftCode + "*" + rightCode;
                    } else if (isLong(rightType)) {
                        resultType = long.class;
                        sourceCode = leftCode + "*" + rightCode;
                    } else if (isDouble(rightType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "*" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot multiply integer to " + rightType.getSimpleName());
                    }
                } else if (isLong(leftType)) {
                    if (isInt(rightType) || isLong(rightType)) {
                        resultType = long.class;
                        sourceCode = leftCode + "*" + rightCode;
                    } else if (isDouble(rightType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "*" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot multiply long to " + rightType.getSimpleName());
                    }
                } else if (isDouble(leftType)) {
                    if (isInt(rightType) || isLong(rightType) || isDouble(leftType)) {
                        resultType = double.class;
                        sourceCode = leftCode + "*" + rightCode;
                    } else {
                        throw new IllegalArgumentException("Cannot multiply double to " + rightType.getSimpleName());
                    }
                } else if (isString(leftType)) {
                    resultType = String.class;
                    if (isInt(rightType) || isLong(rightType)) {
                        sourceCode = "RuntimeUtils.stringMultiplication(" + leftCode + "," + rightCode + ")";
                    } else {
                        throw new IllegalArgumentException("Cannot multiply string to " + rightType.getSimpleName());
                    }
                } else if (isBoolean(leftType)) {
                    throw new IllegalArgumentException("Cannot multiply boolean to " + rightType.getSimpleName());
                } else {
                    throw new IllegalArgumentException("Cannot multiply " + leftType.getSimpleName() + " to " + rightType.getSimpleName());
                }

                sourceCodeStack.push(sourceCode);
                typeStack.push(resultType);
            }

            @Override
            public void visit(GreaterThan greaterThan) {
                super.visit(greaterThan);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                String sourceCode;
                if (isTypesCompatible(leftType, rightType)) {
                    if (isInt(leftType) || isLong(leftType) || isDouble(leftType)) {
                        sourceCode = leftCode + ">" + rightCode;
                    } else if (leftType == String.class) {
                        sourceCode = "RuntimeUtils.stringGreaterThanString(" + leftCode + "," + rightCode + ")";
                    } else if (isBoolean(leftType)) {
                        throw new IllegalArgumentException("Cannot do '>' on boolean types. Expression [" + greaterThan + "]");
                    } else {
                        throw new IllegalArgumentException("Class [" + leftType.getName() + " is not supported");
                    }
                } else {
                    throw new IllegalArgumentException("Error while try to do '>' on non compatible types [" + leftType.getSimpleName() + "] and [" + rightType.getSimpleName() + "]. Expression [" + greaterThan + "]");
                }

                sourceCodeStack.push(sourceCode);
                typeStack.push(boolean.class);
            }

            @Override
            public void visit(GreaterThanEquals greaterThanEquals) {
                super.visit(greaterThanEquals);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                String sourceCode;
                if (isTypesCompatible(leftType, rightType)) {
                    if (isInt(leftType) || isLong(leftType) || isDouble(leftType)) {
                        sourceCode = leftCode + ">=" + rightCode;
                    } else if (leftType == String.class) {
                        sourceCode = "RuntimeUtils.stringGreaterThanString(" + leftCode + "," + rightCode + ")||RuntimeUtils.stringEquals(" + leftCode + "," + rightCode + ")";
                    } else if (isBoolean(leftType)) {
                        throw new IllegalArgumentException("Cannot do '>=' on boolean types. Expression [" + greaterThanEquals + "]");
                    } else {
                        throw new IllegalArgumentException("Class [" + leftType.getName() + " is not supported");
                    }
                } else {
                    throw new IllegalArgumentException("Error while try to do '>' on non compatible types [" + leftType.getSimpleName() + "] and [" + rightType.getSimpleName() + "]. Expression [" + greaterThanEquals + "]");
                }

                sourceCodeStack.push(sourceCode);
                typeStack.push(boolean.class);
            }

            @Override
            public void visit(MinorThan minorThan) {
                super.visit(minorThan);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                String sourceCode;
                if (isTypesCompatible(leftType, rightType)) {
                    if (isInt(leftType) || isLong(leftType) || isDouble(leftType)) {
                        sourceCode = leftCode + "<" + rightCode;
                    } else if (leftType == String.class) {
                        sourceCode = "RuntimeUtils.stringMinorThanString(" + leftCode + "," + rightCode + ")";
                    } else if (isBoolean(leftType)) {
                        throw new IllegalArgumentException("Cannot do '<' on boolean types. Expression [" + minorThan + "]");
                    } else {
                        throw new IllegalArgumentException("Class [" + leftType.getName() + " is not supported");
                    }
                } else {
                    throw new IllegalArgumentException("Error while try to do '<' on non compatible types [" + leftType.getSimpleName() + "] and [" + rightType.getSimpleName() + "]. Expression [" + minorThan + "]");
                }

                sourceCodeStack.push(sourceCode);
                typeStack.push(boolean.class);
            }

            @Override
            public void visit(MinorThanEquals minorThanEquals) {
                super.visit(minorThanEquals);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                String sourceCode;
                if (isTypesCompatible(leftType, rightType)) {
                    if (isInt(leftType) || isLong(leftType) || isDouble(leftType)) {
                        sourceCode = leftCode + "<=" + rightCode;
                    } else if (leftType == String.class) {
                        sourceCode = "RuntimeUtils.stringMinorThanString(" + leftCode + "," + rightCode + ")||RuntimeUtils.stringEquals(" + leftCode + "," + rightCode + ")";
                    } else if (isBoolean(leftType)) {
                        throw new IllegalArgumentException("Cannot do '<=' on boolean types. Expression [" + minorThanEquals + "]");
                    } else {
                        throw new IllegalArgumentException("Class [" + leftType.getName() + " is not supported");
                    }
                } else {
                    throw new IllegalArgumentException("Error while try to do '<=' on non compatible types [" + leftType.getSimpleName() + "] and [" + rightType.getSimpleName() + "]. Expression [" + minorThanEquals + "]");
                }

                sourceCodeStack.push(sourceCode);
                typeStack.push(boolean.class);
            }

            @Override
            public void visit(EqualsTo equalsTo) {
                super.visit(equalsTo);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                String sourceCode;
                if (isTypesCompatible(leftType, rightType)) {
                    if (leftType == int.class || leftType == long.class || leftType == boolean.class) {
                        sourceCode = leftCode + "==" + rightCode;
                    } else if (leftType == float.class) {
                        sourceCode = "Math.abs(" + leftCode + " - " + rightCode + ")<0.0000001f";
                    } else if (leftType == double.class) {
                        sourceCode = "Math.abs(" + leftCode + " - " + rightCode + ")<0.0000001";
                    } else if (leftType == Integer.class || leftType == String.class || leftType == Float.class || leftType == Double.class || leftType == Boolean.class) {
                        sourceCode = "RuntimeUtils.stringEquals(" + leftCode + "," + rightCode + ")";
                    } else {
                        throw new IllegalArgumentException("Class [" + leftType.getName() + " is not supported");
                    }
                } else {
                    throw new IllegalArgumentException("Error while try to do EQUAL on non compatible types [" + leftType.getSimpleName() + "] and [" + rightType.getSimpleName() + "]. Expression [" + equalsTo + "]");
                }

                sourceCodeStack.push(sourceCode);
                typeStack.push(boolean.class);
            }

            @Override
            public void visit(NotEqualsTo notEqualsTo) {
                super.visit(notEqualsTo);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                String sourceCode;
                if (isTypesCompatible(leftType, rightType)) {
                    if (leftType == int.class || leftType == long.class || leftType == boolean.class) {
                        sourceCode = leftCode + "!=" + rightCode;
                    } else if (leftType == float.class) {
                        sourceCode = "Math.abs(" + leftCode + " - " + rightCode + ")>0.0000001f";
                    } else if (leftType == double.class) {
                        sourceCode = "Math.abs(" + leftCode + " - " + rightCode + ")>0.0000001";
                    } else if (leftType == Integer.class || leftType == String.class || leftType == Float.class || leftType == Double.class || leftType == Boolean.class) {
                        sourceCode = "!RuntimeUtils.stringEquals(" + leftCode + "," + rightCode + ")";
                    } else {
                        throw new IllegalArgumentException("Class [" + leftType.getName() + " is not supported");
                    }
                } else {
                    throw new IllegalArgumentException("Error while try to do NOT EQUAL on non compatible types [" + leftType.getSimpleName() + "] and [" + rightType.getSimpleName() + "]. Expression [" + notEqualsTo + "]");
                }

                sourceCodeStack.push(sourceCode);
                typeStack.push(boolean.class);
            }

            @Override
            public void visit(NotExpression notExpr) {
                super.visit(notExpr);
                Class type = typeStack.pop();
                String code = sourceCodeStack.pop();
                if (type != Boolean.class && type != boolean.class) {
                    throw new IllegalArgumentException("NOT operation is applicable only to Boolean type. But you try to apply it to [" + type.getSimpleName() + "]");
                }

                typeStack.push(boolean.class);
                sourceCodeStack.push("!(" + code + ")");
            }

            @Override
            public void visit(Column tableColumn) {
                super.visit(tableColumn);
                String columnValue = tableColumn.toString();
                if (columnValue.equalsIgnoreCase("true") || columnValue.equalsIgnoreCase("false")) {
                    typeStack.add(boolean.class);
                    sourceCodeStack.add(columnValue.toLowerCase());
                } else {
                    SqlField sqlField = tableManager.getSqlFieldByColumn(tableColumn);
                    Field field = sqlField.getTable().getBuffer().getField(sqlField.getField());
                    Class type = field.getType();
                    if (isTypeSupported(type)) {
                        String generatedDataObjectFieldName = "data" + sqlField.getTable().getId();
                        String generatedFieldName = "_" + generatedDataObjectFieldName + "_" + sqlField.getField();
                        String fieldGetName = " "+ rowVariableName +"." + generatedFieldName + "() ";
                        typeStack.add(type);
                        sourceCodeStack.add(fieldGetName);
                    } else {
                        throw new IllegalArgumentException("Field [" + sqlField + "] has unsupported type [" + type.getSimpleName() + "]");
                    }
                }
            }

            @Override
            public void visit(StringValue stringValue) {
                super.visit(stringValue);
                String string = StringEscapeUtils.escapeJava(stringValue.getValue());
                typeStack.add(String.class);
                sourceCodeStack.add("\"" + string + "\"");
            }

            @Override
            public void visit(DoubleValue doubleValue) {
                super.visit(doubleValue);
                String value = doubleValue.toString();
                typeStack.add(double.class);
                sourceCodeStack.add(value);
            }

            @Override
            public void visit(LongValue longValue) {
                super.visit(longValue);
                String value = longValue.toString();
                typeStack.add(long.class);
                sourceCodeStack.add(value);
            }

            @Override
            public void visit(OrExpression orExpression) {
                super.visit(orExpression);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                if (!(isBoolean(leftType) && isBoolean(rightType))) {
                    throw new IllegalArgumentException("OR operation can be used only with boolean values, but you use it like this " + leftType.getSimpleName() + " OR " + rightType.getSimpleName());
                }

                sourceCodeStack.push(leftCode + "||" + rightCode);
                typeStack.push(boolean.class);
            }

            @Override
            public void visit(AndExpression andExpression) {
                super.visit(andExpression);
                Class rightType = typeStack.pop();
                String rightCode = sourceCodeStack.pop();

                Class leftType = typeStack.pop();
                String leftCode = sourceCodeStack.pop();
                if (!(isBoolean(leftType) && isBoolean(rightType))) {
                    throw new IllegalArgumentException("AND operation can be used only with boolean values, but you use it like this " + leftType.getSimpleName() + " AND " + rightType.getSimpleName());
                }

                sourceCodeStack.push(leftCode + "&&" + rightCode);
                typeStack.push(boolean.class);
            }

            @Override
            public void visit(Concat concat) {
                super.visit(concat);
                throw new IllegalArgumentException("Concat operation is not supported, please use +");
            }
        });

        if (sourceCodeStack.size() != 1) {
            throw new IllegalStateException("Expression executor for expression [" + expression + "] generates " + sourceCodeStack.size() + " values, but should generate only 1");
        }

        ExpressionExecutorResult expressionExecutorResult = new ExpressionExecutorResult();
        expressionExecutorResult.expressionExecSourceCode = sourceCodeStack.pop();
        expressionExecutorResult.resultType = typeStack.pop();
        return expressionExecutorResult;
    }

    public static boolean isTypeSupported(Class type) {
        return isString(type)
                || isBoolean(type)
                || isDouble(type)
                || isInt(type)
                || isLong(type);
    }

    public static boolean isTypesCompatible(Class clazz1, Class clazz2) {
        if (isInt(clazz1) || isLong(clazz2)) {
            if (isInt(clazz2) || isLong(clazz2)) {
                return true;
            }
        } else if (isBoolean(clazz1)) {
            if (isBoolean(clazz2)) {
                return true;
            }
        } else if (isString(clazz1)) {
            if (isString(clazz2)) {
                return true;
            }
        } else if (isDouble(clazz1)) {
            if (isDouble(clazz2)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isInt(Class clazz) {
        return clazz == int.class || clazz == Integer.class;
    }

    public static boolean isLong(Class clazz) {
        return clazz == long.class || clazz == Long.class;
    }

    public static boolean isString(Class clazz) {
        return clazz == String.class;
    }

    public static boolean isDouble(Class clazz) {
        return clazz == double.class || clazz == Double.class || clazz == float.class || clazz == Float.class;
    }

    public static boolean isBoolean(Class clazz) {
        return clazz == boolean.class || clazz == Boolean.class;
    }
}
