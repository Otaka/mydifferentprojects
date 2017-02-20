package com.simplepl.grammar;

import com.simplepl.exception.ParseException;
import java.util.Formatter;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.support.Position;

/**
 * @author Dmitry
 */
public class MainParserActions extends BaseParser<Object> {

    private final String errorMessageTemplate = "[Line:%1$d Column:%2$d] %3$s. Found \"%4$s\"";
    private static boolean enableAction = true;

    public static void setEnableAction(boolean enable) {
        enableAction = enable;
    }

    public static boolean isEnableAction() {
        return enableAction;
    }

    public Object error(String message) {
        return new Action<Object>() {
            @Override
            public boolean run(Context<Object> context) {
                throw new ParseException(getContext().getCurrentIndex(), message);
            }
        };
    }

    public Action actionFail(final String message) {
        return new Action() {
            @Override
            public boolean run(Context context) {
                String fullMessage = message;
                if (fullMessage.contains("$match")) {
                    fullMessage = fullMessage.replace("$match", context.getMatch());
                }

                int currentIndex = context.getCurrentIndex();
                Position position = context.getInputBuffer().getPosition(currentIndex);
                String part = context.getInputBuffer().extract(currentIndex, currentIndex + 20);
                part=part.replace("\n", "\\n");
                Formatter formatter = new Formatter();
                formatter.format(errorMessageTemplate, position.line, position.column, fullMessage, part);
                //fullMessage = "[Line:" + position.line + " Column:" + position.column + "]" + fullMessage + " . Found \"" + part + "\"";
                fullMessage = formatter.toString();
                throw new ParseException(currentIndex, fullMessage);
            }
        };
    }

    public Action dbgPrint(final String message) {
        return new Action() {
            @Override
            public boolean run(Context context) {
                String m = message;
                if (m.contains("$match")) {
                    m = m.replace("$match", context.getMatch());
                }

                System.out.println(m);
                return true;
            }
        };
    }

    public static abstract class LangAction implements Action {

        @Override
        public boolean run(Context context) {
            if (!enableAction) {
                return true;
            }

            return runAction(context);
        }

        public abstract boolean runAction(Context context);
    }

    /**
     * Method tries to get the object from the top of the stack, and if it is
     * not section, or the section does not equal to @sectionName, it throws an
     * exception
     */
    private SectionObj popSectionFromTop(String sectionName) {
        Object val = pop();
        if (val == null || !(val instanceof SectionObj)) {
            throw new RuntimeException("Expected section on the top of the stack, but found [" + val + "]");
        }
        SectionObj section = (SectionObj) val;
        if (!sectionName.equals(section.getSectionName())) {
            throw new RuntimeException("Expected section with name [" + sectionName + "], but found [" + section.getSectionName() + "]");
        }
        return section;
    }

    /**
     * Method tries to get the object from the top of the stack, and if it is
     * not stringObj, or the stringObj does not equal to @stringObjName, it
     * throws an exception
     */
    private String popStringObjFromTop(String stringObjName) {
        Object val = pop();
        if (val == null || !(val instanceof StringObj)) {
            throw new RuntimeException("Expected StringObj on the top of the stack, but found [" + val + "]");
        }
        StringObj stringObj = (StringObj) val;
        if (!stringObjName.equals(stringObj.getLabel())) {
            throw new RuntimeException("Expected StringObj with name [" + stringObjName + "], but found [" + stringObj.getLabel() + "]");
        }
        return stringObj.getVal();
    }

    public Action pushHelperString(final String label) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                String value = context.getMatch().trim();
                push(new StringObj(value, label));
                return true;
            }
        };
    }

    public String popHelperString(String label) {
        StringObj string = (StringObj) pop();
        if (!string.getLabel().equals(label)) {
            throw new RuntimeException("On the top of the stack found helperString '" + string.getLabel() + "' but expected '" + label + "'");
        }

        return string.getVal();
    }

    public Action createSectionAndPushLastMatch(final String label, final String key) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                SectionObj section = new SectionObj(label);
                push(section);

                String match = context.getMatch().trim();
                section.pushValue(key, match);
                return true;
            }
        };
    }

    public Action pushValueOnTopSection(final String label, final String key, final Object value) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                SectionObj section = (SectionObj) peek();
                if (section instanceof SectionObj) {
                    if (!section.getSectionName().equals(label)) {
                        throw new RuntimeException("Expected section '" + label + "' on the top of the stack, but found section '" + section.getSectionName() + "'");
                    }
                }

                section.pushValue(key, value);
                return true;
            }
        };
    }

    public Action pushTopStackValueOnTopSection(final String label, final String key) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                Object value = pop();
                SectionObj section = (SectionObj) peek();
                if (section instanceof SectionObj) {
                    if (!section.getSectionName().equals(label)) {
                        throw new RuntimeException("Expected section '" + label + "' on the top of the stack, but found section '" + section.getSectionName() + "'");
                    }
                }

                section.pushValue(key, value);
                return true;
            }
        };
    }

    public Action pushLastMatchOnTopSection(final String label, final String key) {
        return new LangAction() {
            @Override
            public boolean runAction(Context context) {
                String value = lastMatch();
                SectionObj section = (SectionObj) peek();
                if (section instanceof SectionObj) {
                    if (!section.getSectionName().equals(label)) {
                        throw new RuntimeException("Expected section '" + label + "' on the top of the stack, but found section '" + section.getSectionName() + "'");
                    }
                }

                section.pushValue(key, value);
                return true;
            }
        };
    }

    private String lastMatch() {
        return getContext().getMatch().trim();
    }

}
