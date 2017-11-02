package com.simplepl.astinfoextractor;

import com.simplepl.entity.Context;
import com.simplepl.entity.ModuleInfo;
import com.simplepl.exception.ParseException;
import com.simplepl.grammar.MainParser;
import com.simplepl.grammar.ast.Ast;
import com.simplepl.vfs.AbstractFile;
import java.util.HashMap;
import java.util.Map;
import org.parboiled.Parboiled;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.support.ParsingResult;

/**
 * @author sad
 */
public class AstManager {

    private Context context;
    private Map<String, Ast> parsedSourceFiles = new HashMap<>();
    private Map<String, ModuleInfo> moduleInfoMap = new HashMap<String, ModuleInfo>();

    public AstManager(Context context) {
        this.context = context;
    }

    public Ast getModuleAst(String modulePath) {
        Ast ast = parsedSourceFiles.get(modulePath.toLowerCase());
        if (ast != null) {
            return ast;
        }

        AbstractFile file = context.getSrcRoot().getAbstractFile(modulePath);
        String sourceText = file.readToString();
        Ast moduleAst = parseSource(sourceText, modulePath);
        parsedSourceFiles.put(modulePath, ast);
        return moduleAst;
    }

    private Ast parseSource(String sourceText, String module) {
        MainParser parser = createParser();
        ParseRunner runner = new BasicParseRunner(parser.main());
        ParsingResult<Object> articleResult = runner.run(sourceText);
        if (!articleResult.matched) {
            System.out.println("ERROR while parsing module " + module);
            for (ParseError error : articleResult.parseErrors) {
                System.out.println("" + error.getStartIndex() + error.getErrorMessage());
            }
            throw new ParseException(0, "Cannot compile module " + module);
        }

        Ast ast = (Ast) articleResult.valueStack.pop();
        if (!articleResult.valueStack.isEmpty()) {
            Object obj = articleResult.valueStack.pop();
            throw new IllegalStateException("Internal compiler error while parsing " + module + ". Value stack should have only one value at the end, but it contains [" + obj);
        }

        return ast;
    }

    private MainParser createParser() {
        MainParser parser = Parboiled.createParser(MainParser.class);
        return parser;
    }

    public ModuleInfo getModuleInfo(String module) {
        ModuleInfo moduleInfo = moduleInfoMap.get(module.toLowerCase());
        if (moduleInfo == null) {
            Ast moduleAst = getModuleAst(module);
            PublicEntitiesExtractor entitiesExtractor = new PublicEntitiesExtractor();
            moduleInfo = entitiesExtractor.processAst(moduleAst, module);
            moduleInfoMap.put(module.toLowerCase(), moduleInfo);
        }

        return moduleInfo;
    }
}
