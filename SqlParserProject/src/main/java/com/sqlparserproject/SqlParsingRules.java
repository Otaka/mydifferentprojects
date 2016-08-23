package com.sqlparserproject;

import org.parboiled.Action;
import org.parboiled.Context;
import org.parboiled.Rule;

/**
 * @author sad
 */
public class SqlParsingRules extends SqlParserActions {

    public Rule start() {
        return Sequence(
                pushSqlListAst(),
                OneOrMore(
                        Sequence(
                                sqlExpressionLine(),
                                putSqlToSqlListAst()
                        )
                ),
                EOI);
    }

    public Rule sqlExpressionLine() {
        return Sequence(Whitespace(), sqlExpression(), Optional(token(";")));
    }

    public Rule testSqlExpressionLine() {
        return Sequence(sqlExpressionLine(), EOI);
    }

    public Rule sqlExpression() {
        return FirstOf(
                commit(),
                updateExpression(),
                insertInto(),
                dropTableRule(),
                createTableRule(),
                selectExpression()
        //actionFail("Cannot parse sql query")
        );
    }

    public Rule testInsertRule() {
        return Sequence(insertInto(), EOI);
    }

    public Rule testUpdateRule() {
        return Sequence(updateExpression(), EOI);
    }

    public Rule commit() {
        return Sequence(tokenIgnCs("commit"), pushCommitAst());
    }

    public Rule updateExpression() {
        return Sequence(
                tokenIgnCs("update"),
                FirstOf(
                        Sequence(
                                FirstOf(
                                        Sequence(name(), pushUpdateAst()),
                                        actionFail("Expected table name after 'UPDATE'")
                                ),
                                FirstOf(
                                        tokenIgnCs("set"),
                                        actionFail("Expected 'SET' after UPDATE TABLENAME...")
                                ),
                                FirstOf(
                                        Sequence(updateSetListOfFields(), putListOfFieldsIntoTheUpdateAst()),
                                        actionFail("Expected list of column=value pairs after UPDATE TABLENAME SET ...")
                                ),
                                Optional(
                                        Sequence(
                                                tokenIgnCs("from"),
                                                fromExpression(),
                                                putFromExpressionToUpdateAst()
                                        )
                                ),
                                Optional(
                                        whereExpression(),
                                        putWhereExpressionToUpdateAst()
                                )
                        ),
                        actionFail("error in update expression")
                )
        );
    }

    public Rule updateSetListOfFields() {
        return Sequence(
                pushUpdateListOfFieldAst(),
                updateSetPair(),
                putFieldPairInUpdateAst(),
                ZeroOrMore(
                        Sequence(
                                token(","),
                                updateSetPair(),
                                putFieldPairInUpdateAst()
                        )
                )
        );
    }

    public Rule updateSetPair() {
        return Sequence(
                name(), pushHelperString("updateSetPair_FieldName"),
                token("="),
                checkExpression(),
                pushUpdateSetPairAst()
        );
    }

    public Rule insertInto() {
        return Sequence(
                tokenIgnCs("insert"),
                FirstOf(
                        Sequence(
                                FirstOf(
                                        tokenIgnCs("into"),
                                        actionFail("Expected 'INTO' after 'INSERT'")
                                ),
                                FirstOf(
                                        Sequence(name(), pushInsertAst()),
                                        actionFail("Expected table name after 'INSERT INTO'")
                                ),
                                FirstOf(
                                        token("("),
                                        actionFail("Expected '(column list)' after 'INSERT INTO TABLENAME ...'")
                                ),
                                FirstOf(
                                        Sequence(columnListForInsert(), setColumnListToInsertAst()),
                                        actionFail("Expected column list for INSERT INTO TABLENAME(...)")
                                ),
                                FirstOf(
                                        token(")"),
                                        actionFail("Expect closing bracket after INSERT INTO TABLENAME (COLUMN LIST...")
                                ),
                                Optional(
                                        Sequence(
                                                tokenIgnCs("values"),
                                                actionFail("Cannot insert the data. Only insert from select is supported now")
                                        )
                                ),
                                FirstOf(
                                        Sequence(selectExpression(), setSelectAstToInsertAst()),
                                        actionFail("Expected Select expression after insert into tableName(col,col,col)...")
                                )
                        ),
                        actionFail("Error in insert expression")
                )
        );
    }

    public Rule columnListForInsert() {
        return Sequence(
                Sequence(name(), pushInsertColumnListAst()),
                pushColumnToInsertColumnListAst(),
                ZeroOrMore(
                        token(","),
                        name(),
                        pushColumnToInsertColumnListAst()
                )
        );
    }

    public Rule dropTableRule() {
        return Sequence(
                tokenIgnCs("drop"),
                tokenIgnCs("table"),
                pushDropTableAst(),
                FirstOf(
                        Sequence(
                                Optional(
                                        Sequence(
                                                tokenIgnCs("if"),
                                                tokenIgnCs("exists"),
                                                setDropTableIfExists()
                                        )
                                ),
                                name(),
                                setDropTableName(),
                                Optional(
                                        Sequence(
                                                tokenIgnCs("cascade"),
                                                setDropTableCascade()
                                        )
                                )
                        ),
                        actionFail("Error in DROP TABLE expression")
                )
        );
    }

    public Rule testCreateTable() {
        return Sequence(createTableRule(), EOI);
    }

    public Rule createTableRule() {
        return Sequence(
                tokenIgnCs("create"),
                pushCreateTableAst(),
                FirstOf(
                        Sequence(
                                Optional(Sequence(tokenIgnCs("local"), tokenIgnCs("temp"), setLocalTempToCreateTableAst())),
                                FirstOf(
                                        tokenIgnCs("table"),
                                        actionFail("expected 'Table' keyword")
                                ),
                                FirstOf(
                                        Sequence(tableNameWithAlias(), setTableNameToCreateTableAst()),
                                        actionFail("Expected table name")
                                ),
                                Optional(
                                        Sequence(tokenIgnCs("on"),
                                                FirstOf(
                                                        Sequence(
                                                                tokenIgnCs("commit"),
                                                                tokenIgnCs("preserve"),
                                                                tokenIgnCs("rows"),
                                                                setOnCommitCreateRowsInCreateTableAst()
                                                        ),
                                                        actionFail("after 'ON' expected 'COMMIT PRESERVE ROWS'")
                                                )
                                        )
                                ),
                                FirstOf(
                                        Sequence(
                                                tokenIgnCs("as"),
                                                FirstOf(
                                                        Sequence(selectExpression(), setSelectAstToCreateTableAst()),
                                                        actionFail("Expected 'SELECT' expression")
                                                )
                                        ),
                                        Sequence(
                                                tokenIgnCs("like"),
                                                FirstOf(
                                                        Sequence(tableNameWithoutAlias(), setLikeTableToCreateTableAst()),
                                                        actionFail("Expected table name")
                                                )
                                        ),
                                        Sequence(
                                                tokenIgnCs("("),
                                                tokenIgnCs(")"),
                                                actionFail("not implemented create table with explicit config. Done only \"create as select\"")
                                        ),
                                        actionFail("Cannot parse CREATE TABLE...")
                                ),
                                Optional(
                                        Sequence(
                                                tokenIgnCs("including"),
                                                tokenIgnCs("projections"),
                                                setIncludingProjectionsToCreateTableAst()
                                        )
                                )
                        ),
                        actionFail("Error in CREATE TABLE expression")
                )
        );
    }

    public Rule testSelectExpression() {
        return Sequence(selectExpression(), EOI);
    }

    public Rule selectImplementation() {
        return FirstOf(
                Sequence(
                        Optional(
                                tokenIgnCs("distinct"), setSelectDistinct()
                        ),
                        FirstOf(
                                Sequence(columnListExpression(), setFieldListToSelectAst()),
                                actionFail("Expected column list")
                        ),
                        FirstOf(
                                tokenIgnCs("from"),
                                actionFail("Expected 'From' keyword")
                        ),
                        FirstOf(
                                Sequence(fromExpression(), putFromToSelect()),
                                actionFail("Error in 'from' expression")
                        ),
                        ZeroOrMore(joinRule(), addJoinToSelectAst()),
                        Optional(whereExpression(), putWhereToSelect()),
                        Optional(groupByRule(), putGroupByToSelect()),
                        Optional(orderByRule(), putOrderByToSelect()),
                        ZeroOrMore(
                                Sequence(
                                        tokenIgnCs("union"),
                                        pushUnionAst(),
                                        Optional(
                                                tokenIgnCs("all"),
                                                setUnionAsAll()
                                        ),
                                        FirstOf(
                                                Sequence(selectExpression(), setSecondSelectToUnionAst()),
                                                actionFail("Expected Select expression after 'UNION' keyword")
                                        )
                                )
                        )
                ),
                actionFail("Error in select query")
        );
    }

    public Rule selectExpression() {
        return Sequence(
                Sequence(
                        tokenIgnCs("select"),
                        pushSelectAst(false)
                ),
                selectImplementation()
        );
    }
    
    public Rule subquerySelectExpression() {
        return Sequence(
                Sequence(
                        tokenIgnCs("select"),
                        pushSelectAst(true)
                ),
                selectImplementation()
        );
    }

    public Rule testGroupByRule() {
        return Sequence(groupByRule(), EOI);
    }

    public Rule joinRule() {
        return Sequence(
                FirstOf(
                        Sequence(tokenIgnCs("left"), Optional(tokenIgnCs("outer")), tokenIgnCs("join")),
                        Sequence(tokenIgnCs("right"), Optional(tokenIgnCs("outer")), tokenIgnCs("join")),
                        Sequence(tokenIgnCs("inner"), tokenIgnCs("join")),
                        Sequence(tokenIgnCs("full"), tokenIgnCs("join")),
                        tokenIgnCs("join")
                ),
                pushJoinAst(),
                FirstOf(
                        Sequence(
                                FirstOf(
                                        tableNameWithAlias(),
                                        Sequence(checkSelectExpression(), Optional(name(), wrapLastTableAstInAliasAst())),
                                        actionFail("expected table name")
                                ),
                                putTableToJoin(),
                                FirstOf(
                                        tokenIgnCs("on"),
                                        actionFail("Expected 'ON'")
                                ),
                                FirstOf(
                                        checkExpression(),
                                        actionFail("Expected join check expression")
                                ),
                                putCheckExpressionOnJoin()
                        ),
                        actionFail("Cannot process body of the join")
                )
        );
    }

    public Rule groupByRule() {
        return Sequence(
                tokenIgnCs("group"),
                FirstOf(
                        tokenIgnCs("by"),
                        actionFail("Expected 'by' after 'group'")
                ),
                pushGroupBy(),
                FirstOf(
                        Sequence(groupAndOrderColumnsList(), putOrderGroupByFieldListToGroupByAst()),
                        actionFail("Expected list of columns for grouping")
                )
        );
    }

    public Rule testOrderBy() {
        return Sequence(orderByRule(), EOI);
    }

    public Rule orderByRule() {
        return Sequence(
                tokenIgnCs("order"),
                FirstOf(
                        tokenIgnCs("by"),
                        actionFail("Expected 'by' after 'order'")
                ),
                pushOrderBy(),
                FirstOf(
                        Sequence(groupAndOrderColumnsList(), putOrderGroupByFieldListToOrderByAst()),
                        actionFail("Expected list of columns for order by")
                )
        );
    }

    public Rule groupAndOrderColumnsList() {
        return Sequence(
                pushOrderGroupByFieldListAst(),
                FirstOf(
                        functionInvocation(),
                        columnNameWithStarExpression()
                ),
                pushOrder_GroupByFieldAst(),
                Optional(
                        FirstOf(
                                tokenIgnCs("asc"),
                                tokenIgnCs("desc")
                        ),
                        putAsc_Desc_To_OrderGroupByFieldAst()
                ),
                putOrderGroupByFieldToListAst(),
                ZeroOrMore(
                        Sequence(
                                token(","),
                                FirstOf(
                                        functionInvocation(),
                                        columnNameWithStarExpression()
                                ),
                                pushOrder_GroupByFieldAst(),
                                Optional(
                                        FirstOf(
                                                tokenIgnCs("asc"),
                                                tokenIgnCs("desc")
                                        ),
                                        putAsc_Desc_To_OrderGroupByFieldAst()
                                ),
                                putOrderGroupByFieldToListAst()
                        )
                )
        );
    }

    public Rule columnListExpression() {
        return Sequence(
                pushFieldListAst(),
                columnExpression(),
                putFieldAstToFieldList(),
                ZeroOrMore(
                        Sequence(
                                token(","),
                                columnExpression(),
                                putFieldAstToFieldList()
                        )
                )
        );
    }

    public Rule typeConversion() {
        return Optional(
                Sequence(
                        token("::"),
                        FirstOf(
                                Sequence(
                                        typeConversionVariantRule(),
                                        pushTypeConversionAst()
                                ),
                                actionFail("Expected type after the '::' type conversion")
                        )
                )
        );
    }

    public Rule testTypeConversionVariantRule() {
        return Sequence(typeConversionVariantRule(), EOI);
    }

    public Rule typeConversionVariantRule() {
        return Sequence(
                FirstOf(
                        Sequence(
                                tokenIgnCs("varchar"),
                                createSectionAndPushLastMatch(TYPE_CONVERSION, "type"),
                                FirstOf(
                                        token("("),
                                        actionFail("Expected number of characters in varchar. Example varchar(34)")
                                ),
                                FirstOf(
                                        Sequence(
                                                number(),
                                                pushTopStackValueOnTopSection(TYPE_CONVERSION, "size")
                                        ),
                                        actionFail("Expected size of the varchar after varchar(")
                                ),
                                FirstOf(
                                        token(")"),
                                        actionFail("Expected closing bracket after the size of the varchar")
                                )
                        ),
                        Sequence(
                                tokenIgnCs("numeric"),
                                createSectionAndPushLastMatch(TYPE_CONVERSION, "type"),
                                FirstOf(
                                        token("("),
                                        actionFail("Expected size arguments in numeric. Example numeric(12,10)")
                                ),
                                FirstOf(
                                        Sequence(number(), pushTopStackValueOnTopSection(TYPE_CONVERSION, "size1")),
                                        actionFail("Expected size of the numeric after numeric(")
                                ),
                                Optional(
                                        Sequence(
                                                tokenIgnCs(","),
                                                FirstOf(
                                                        Sequence(number(), pushTopStackValueOnTopSection(TYPE_CONVERSION, "size2")),
                                                        actionFail("Expected second size argument after the comma in numeric(x,...")
                                                )
                                        )
                                ),
                                FirstOf(
                                        token(")"),
                                        actionFail("Expected closing bracket after the size of the numeric")
                                )
                        ),
                        Sequence(tokenIgnCs("int"), createSectionAndPushLastMatch(TYPE_CONVERSION, "type")),
                        Sequence(tokenIgnCs("varbinary"), createSectionAndPushLastMatch(TYPE_CONVERSION, "type")),
                        actionFail("Unknown conversion type")
                ),
                pushSqlType()
        );
    }

    public Rule subqueryColumnRule() {
        return Sequence(
                token("("),
                subquerySelectExpression(),
                token(")")
        );
    }

    public Rule extractFromEpochRule() {
        return Sequence(
                tokenIgnCs("extract"),
                token("("),
                tokenIgnCs("epoch"),
                tokenIgnCs("from"),
                checkExpression(),
                token(")"),
                pushExtractFromEpochAst()
        );
    }

    public Rule columnExpression() {
        return Sequence(
                Sequence(
                        FirstOf(
                                subqueryColumnRule(),
                                extractFromEpochRule(),
                                castFunction(),
                                stringRule(),
                                caseRule(),
                                Sequence(token("*"), pushColumnAsteriskAst()),
                                checkExpression()
                        ),
                        typeConversion(),
                        Optional(Sequence(
                                        tokenIgnCs("as"),
                                        FirstOf(
                                                Sequence(name(), pushHelperString("aliasName"), pushColumnAliasAst()),
                                                actionFail("Expected alias name after 'as' keyword")
                                        )
                                )
                        )
                ),
                ZeroOrMore(
                        Sequence(
                                token("||"),
                                FirstOf(
                                        Sequence(columnExpression(), pushConcatenateAst()),
                                        actionFail("Please provide columnExpression after '||' concatenation symbol")
                                )
                        )
                )
        );
    }

    public Rule castFunction() {
        return Sequence(
                tokenIgnCs("cast"),
                tokenIgnCs("("),
                checkExpression(),
                tokenIgnCs("as"),
                typeConversionVariantRule(),
                tokenIgnCs(")"),
                pushCastFunction()
        );
    }

    /*public Rule functionWithPartitionBy() {
     return Sequence(
     FirstOf(
     tokenIgnCs("ROW_NUMBER"),
     tokenIgnCs("SUM"),
     tokenIgnCs("LAG"),
     tokenIgnCs("LEAD"),
     tokenIgnCs("max"),
     tokenIgnCs("min")
     ),
     Sequence(
     tokenIgnCs("("),
     Optional(functionArguments()),
     tokenIgnCs(")")
     ),
     overPartitionBy()
     );
     }*/
    public Rule overPartitionBy() {
        return Sequence(
                tokenIgnCs("over"),
                tokenIgnCs("("),
                FirstOf(
                        partitionBy(),
                        actionFail("Expected 'PARTITION BY' as argument of the X() OVER(...)")
                ),
                tokenIgnCs(")")
        );
    }

    public Rule partitionBy() {
        return Sequence(
                tokenIgnCs("partition"),
                tokenIgnCs("by"),
                pushOverPartitionByAst(),
                name(),
                setFieldToOverPartitionByAst(),
                ZeroOrMore(
                        Sequence(
                                token(","),
                                name(),
                                setFieldToOverPartitionByAst()
                        )
                ),
                Optional(
                        orderByRule(),
                        setOrderByToOverPartitionByAst()
                )//,
        //Optional(name())
        );
    }

    public Rule functionArguments() {
        return Sequence(
                columnExpression(),
                setFunctionArgumentAst(),
                ZeroOrMore(
                        Sequence(
                                tokenIgnCs(","),
                                columnExpression(),
                                setFunctionArgumentAst()
                        )
                )
        );
    }

    public Rule functionInvocation() {
        return Sequence(
                name(),
                pushFunctionAst(),
                token("("),
                Optional(
                        functionArguments()
                ),
                FirstOf(
                        token(")"),
                        actionFail("Expected close bracket ')' of the argument list of function")
                ),
                Optional(overPartitionBy(), setOverPartitionByToFunctionAst())
        );
    }

    public Rule columnNameWithStarExpression() {
        return Sequence(
                FirstOf(
                        Sequence(
                                Sequence(
                                        name(),
                                        token("."),
                                        token(",")
                                ),
                                actionFail("Error in column list ")
                        ),//wrong
                        Sequence(
                                name(),
                                createSectionAndPushLastMatch("columnSection", "columnTable"),
                                token("."),
                                FirstOf(
                                        token("*"),
                                        name()
                                ),
                                pushLastMatchOnTopSection("columnSection", "columnName")
                        ),//alias.columnName
                        Sequence(name(), createSectionAndPushLastMatch("columnSection", "columnName"))//columnName
                ),
                pushColumnNameAst()
        );
    }

    public Rule columnNameExpression() {
        return Sequence(
                FirstOf(
                        extractFromEpochRule(),
                        Sequence(functionInvocation(), typeConversion()),
                        Sequence(Sequence(name(), token("."), token(",")), actionFail("Error in column list ")),//wrong
                        Sequence(name(), pushHelperString("tableName"), token("."), name(), pushHelperString("fieldName"), pushReadingFieldWithTableAst()),//alias.columnName
                        Sequence(name(), pushHelperString("tableName"), token("."), token("*"), pushHelperString("fieldName"), pushReadingFieldWithTableAst()),//alias.*
                        Sequence(name(), pushHelperString("fieldName"), pushReadingFieldWithoutTableAst())//columnName,
                ),
                Optional(
                        betweenRule()
                )
        );
    }

    public Rule betweenRule() {
        return Sequence(tokenIgnCs("between"), number(), tokenIgnCs("and"), number(), pushBetweenAst());
    }

    public Rule fromExpression() {
        return Sequence(
                pushFromAst(),
                singleFromRule(),
                addSingleFromRuleToFromAst(),
                ZeroOrMore(
                        token(","),
                        singleFromRule(),
                        addSingleFromRuleToFromAst()
                )
        );
    }

    public Rule singleFromRule() {
        return FirstOf(
                //from select
                Sequence(
                        token("("),
                        FirstOf(
                                subquerySelectExpression(),
                                actionFail("Expected select expression as subquery of FROM(...)")
                        ),
                        FirstOf(
                                token(")"),
                                actionFail("Expected close bracket after subquery of FROM(...")
                        ),
                        Optional(
                                Sequence(
                                        name(),
                                        pushFromSubqueryAliasAst()
                                )
                        )//optional alias of the anonimous table "select myalias.* from (select...) myalias"
                ),
                tableNameWithAlias(),
                actionFail("Error in FROM expression. Should be table with optional alias, or subquery with optional alias")
        );
    }

    public Rule whereExpression() {
        return Sequence(
                tokenIgnCs("where"),
                FirstOf(
                        Sequence(checkExpression(), pushWhereAst()),
                        actionFail("Error in where expression")
                )
        );
    }

    public Rule testWhereExpression() {
        return Sequence(whereExpression(), EOI);
    }

    public Rule testValuesListForInExpression() {
        return Sequence(valuesListForInExpression(), EOI);
    }

    public Rule valuesListForInExpression() {
        return Sequence(
                pushInValueList(),
                FirstOf(
                        Sequence(
                                number(),
                                addValueToInValueList(),
                                ZeroOrMore(
                                        token(","),
                                        number(),
                                        addValueToInValueList()
                                )
                        ),
                        Sequence(
                                stringRule(),
                                addValueToInValueList(),
                                ZeroOrMore(
                                        token(","),
                                        stringRule(),
                                        addValueToInValueList()
                                )
                        ),
                        actionFail("in the IN(...) expression should be subquery or list of numbers/strings")
                )
        );
    }

    public Rule checkExpression() {
        return Sequence(
                FirstOf(
                        Sequence(
                                tokenIgnCs("not"),
                                FirstOf(
                                        Sequence(checkExpression(), pushNotAst()),
                                        actionFail("Expected expression after 'not'")
                                )
                        ),
                        Sequence(
                                equalRule(),
                                ZeroOrMore(
                                        FirstOf(
                                                tokenIgnCs("and"),
                                                tokenIgnCs("or")
                                        ),
                                        pushHelperString("and/or_operation"),
                                        FirstOf(
                                                equalRule(),
                                                actionFail("after 'and'/'or' should be another expression")
                                        ),
                                        pushBinaryOperationRule()
                                )
                        )
                ),
                ZeroOrMore(
                        token("||"),
                        FirstOf(
                                Sequence(checkExpression(), pushConcatenateAst()),
                                actionFail("Please provide columnExpression after '||' concatenation symbol")
                        )
                )
        );
    }

    public Rule equalRule() {
        return FirstOf(
                Sequence(tokenIgnCs("not"), checkExpression(), pushNotAst()),
                Sequence(
                        columnNameExpression(),
                        createSectionAndPushLastMatch("in_section", "columnName"),
                        Optional(
                                tokenIgnCs("not"),
                                pushValueOnTopSection("in_section", "not", "true")
                        ),
                        tokenIgnCs("in"),
                        FirstOf(
                                token("("),
                                actionFail("Expected '(' after 'IN'")
                        ),
                        FirstOf(
                                Sequence(subquerySelectExpression(), pushInFromSelectAst()),
                                Sequence(valuesListForInExpression(), pushInFromValueListAst()),
                                actionFail("Expected select subquery or number/string list after the 'IN' expression")
                        ),
                        FirstOf(
                                token(")"),
                                actionFail("Expected ')' after the IN(...")
                        )
                ),
                Sequence(
                        tokenIgnCs("exists"),
                        FirstOf(
                                token("("),
                                actionFail("Expected '(' after 'EXISTS'")
                        ),
                        FirstOf(
                                subquerySelectExpression(),
                                actionFail("Expected 'SELECT' expression in EXISTS(...) statement")
                        ),
                        pushExistsAst(),
                        FirstOf(
                                token(")"),
                                actionFail("Expected ')' after EXISTS(select...")
                        )
                ),
                Sequence(
                        sumRule(),
                        ZeroOrMore(
                                FirstOf(token("!="), token("="), token(">="), token("<="), token(">"), token("<"), tokenIgnCs("is")),
                                pushHelperString("equalOperation"),
                                FirstOf(
                                        sumRule(),
                                        actionFail("Expected expression after comparing operation")
                                ),
                                pushBinaryOperationRule()
                        )
                )
        );
    }

    public Rule sumRule() {
        return FirstOf(
                Sequence(tokenIgnCs("not"), NULL(), pushNotAst()),
                caseRule(),
                Sequence(
                        term(),
                        ZeroOrMore(
                                FirstOf(token("+"), token("-")),
                                pushHelperString("+/- operation"),
                                FirstOf(
                                        term(),
                                        actionFail("Expected expression after +/-")
                                ),
                                pushBinaryOperationRule()
                        )
                ));
    }

    public Rule term() {
        return Sequence(
                atom(),
                ZeroOrMore(
                        FirstOf(token("*"), token("/")),
                        pushHelperString("* or / operation"),
                        FirstOf(
                                atom(),
                                actionFail("Exxpected expression after * or /")
                        ),
                        pushBinaryOperationRule()
                )
        );
    }

    public Rule atom() {
        return Sequence(
                FirstOf(
                        NULL(),
                        number(),
                        booleanValueRule(),
                        stringRule(),
                        columnNameExpression(),
                        parens()
                ),
                typeConversion()
        );
    }

    public Rule booleanValueRule() {
        return Sequence(
                FirstOf(
                        tokenIgnCs("true"),
                        tokenIgnCs("false")
                ),
                pushBooleanAst()
        );
    }

    public Rule NULL() {
        return Sequence(tokenIgnCs("null"), pushNullAst());
    }

    public Rule parens() {
        return Sequence(
                token("("),
                checkExpression(),
                pushParenthesisAst(),
                token(")")
        );
    }

    public Rule checkSelectExpression() {
        return Sequence(token("("), subquerySelectExpression(), token(")"));
    }

    public Rule number() {
        return Sequence(
                // we use another Sequence in the "number" Sequence so we can easily access the input text matched
                // by the three enclosed rules with "match()" or "matchOrDefault()"
                Sequence(
                        Optional('-'),
                        OneOrMore(Digit()),
                        Optional('.', OneOrMore(Digit()))
                ),
                pushNumberAst(),
                // the matchOrDefault() call returns the matched input text of the immediately preceding rule
                // or a default string (in this case if it is run during error recovery (resynchronization))
                Whitespace()
        );
    }

    public Rule Digit() {
        return Sequence(Whitespace(), OneOrMore(CharRange('0', '9')), Whitespace());
    }

    public Rule stringRule() {
        return Sequence(
                Sequence(
                        Whitespace(),
                        "'", STR_TERMINAL('\''),
                        Whitespace()
                ),
                pushStringAst()
        );
    }

    public Rule createTableConfig() {
        return Test(new Action() {
            @Override
            public boolean run(Context context) {
                throw new RuntimeException("cannot create table with explicit configuration. Can create only with 'as select'");
            }
        });
    }

    public Rule tableNameWithAlias() {
        return Sequence(
                name(),
                pushTableNameAst(),
                Optional(
                        Sequence(
                                name(), pushTableAliasAst()
                        )
                )
        );
    }

    public Rule tableNameWithoutAlias() {
        return Sequence(name(), pushTableNameAst());
    }

    public Rule name() {
        return //check name should not be any of these
                Sequence(
                        Sequence(
                                OneOrMore(
                                        FirstOf(
                                                CharRange('a', 'z'),
                                                CharRange('A', 'Z'),
                                                String("_"),
                                                CharRange('0', '9')
                                        )
                                ),
                                Whitespace()
                        ),
                        new Action() {
                            @Override
                            public boolean run(Context context) {
                                String token = context.getMatch().trim();
                                String tokenLow = token.toLowerCase();

                                return !checkTokenEqualTo(tokenLow, "including", "projections", "as", "else", "values", "from", "exists", "between", "cast", "create", "table", "true", "false", "select", "drop", "where", "left", "right", "inner", "outer", "join", "full", "union", "distinct", "on", "when", "case", "end", "group", "order", "by", "and", "or", "all", "in", "is", "null", "not", "insert", "into", "asc", "desc", "like", "update", "local", "temp");
                            }
                        }
                );
    }

    public Rule testCaseRule() {
        return Sequence(caseRule(), EOI);
    }

    public Rule caseRule() {
        return Sequence(
                tokenIgnCs("case"),
                pushCaseAst(),
                FirstOf(
                        OneOrMore(
                                tokenIgnCs("when"),
                                FirstOf(
                                        checkExpression(),
                                        actionFail("Expected check expression after 'WHEN' in 'CASE'")
                                ),
                                FirstOf(
                                        tokenIgnCs("then"),
                                        actionFail("Expected 'THEN' after 'CASE WHEN CHECK_EXPRESSION'")
                                ),
                                FirstOf(
                                        checkExpression(),
                                        actionFail("Expected expression after 'CASE WHEN CHECK_EXPRESSION THEN...' ")
                                ),
                                pushCaseRuleAst()
                        ),
                        actionFail("Cannot parse case body")
                ),
                Optional(
                        Sequence(
                                tokenIgnCs("else"),
                                FirstOf(
                                        checkExpression(),
                                        actionFail("Expected expression after 'CASE WHEN THEN ELSE ...'")
                                ),
                                pushCaseElseRuleAst()
                        )
                ),
                FirstOf(
                        tokenIgnCs("end"),
                        actionFail("Expected 'END' after CASE WHEN expression")
                )
        );
    }

    private boolean checkTokenEqualTo(String token, String... toMatch) {
        for (String v : toMatch) {
            if (token.equals(v)) {
                return true;
            }
        }

        return false;
    }

    public Rule STR_TERMINAL(char... character) {
        return Sequence(ZeroOrMore(NoneOf(character)), character);
    }

    public Rule Whitespace() {
        return ZeroOrMore(FirstOf(' ', '\t', '\r', '\n'));
    }

    public Rule token(String tokenLine) {
        return Sequence(Whitespace(), tokenLine, Whitespace());
    }

    public Rule tokenIgnCs(String tokenLine) {
        return Sequence(Whitespace(), IgnoreCase(tokenLine), Whitespace());
    }
}
