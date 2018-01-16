package com;

import com.macro.exception.MacrosRuntimeException;
import com.macro.functions.AbstrMacroFunction;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sad
 */
public class LikeFunction extends AbstrMacroFunction {

    @Override
    public String process(List<String> args) {
        if (args.size() <= 1) {
            throw new MacrosRuntimeException("LIKE macrofunction should have 2 or more arguments");
        }

        StringBuilder sb = new StringBuilder();
        String first = args.get(0);
        for (int i = 1; i < args.size(); i++) {
            if (i != 1) {
                sb.append("||");
            }
            String arg = args.get(i);
            if (!arg.startsWith("'") && !arg.startsWith("\"")) {
                throw new MacrosRuntimeException("The " + i + " argument [" + arg + "]of the macro function LIKE should be explicit string literal.");
            }

            if (arg.contains("%%")) {
                throw new MacrosRuntimeException("The " + i + " argument [" + arg + "] of the macro function LIKE should not contain '%%' symbols, because it is wildcards");
            }

            String result = parseWildcardString(first, arg);
            sb.append(result);

        }

        return sb.toString();
    }

    /**
     Parse the '%string%' or '%string' line and produce .contains('string') or .endsWith('string') result<br>
     Wildcard string also can contain # - number. In result we will get something like this %ge-#/#/#. This will be converted to regular expression
     */
    private String parseWildcardString(String varToCompare, String value) {
        char quoteSymbol = value.charAt(0);
        value = StringUtils.strip(value, "" + quoteSymbol);

        boolean startsWithPercent = false;
        boolean endsWithPercent = false;
        if (value.startsWith("%")) {
            startsWithPercent = true;
        }

        if (value.endsWith("%")) {
            endsWithPercent = true;
        }

        value = StringUtils.strip(value, "%");

        if (value.contains("#")) {
            //should be converted to regexp
            return processComplexWildcard(varToCompare, value, quoteSymbol, startsWithPercent, endsWithPercent);
        }

        if (!value.contains("%")) {//possible cases 'val','%val','val%','%val%'
            return returnWildcardFunction(varToCompare, value, quoteSymbol, startsWithPercent, endsWithPercent);
        } else {
            return processMultipartWildcardString(varToCompare, value, startsWithPercent, endsWithPercent, quoteSymbol);
        }
    }

    /**
     This wildcard should be converted to regexp
     */
    private String processComplexWildcard(String var, String string, char quoteSymbol, boolean startsWithPercent, boolean endsWithPercent) {
        String regexp = convertWildcardToRegEx(string);
        if (!startsWithPercent) {
            regexp="^"+regexp;
        }
        if (!endsWithPercent) {
            regexp+="$";
        }
        return "likeRegexp("+var+","+quoteSymbol+regexp+quoteSymbol+")";
    }

    private String convertWildcardToRegEx(String line) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); ++i) {
            final char c = line.charAt(i);
            switch (c) {
                case '%':
                    sb.append(".*?");
                    break;

                case '#':
                    sb.append("\\\\d+");
                    break;
                case ']':
                case '.':
                case '[':
                case '^':
                case '\\':
                case '$':
                case '?':
                case '{':
                case '}':
                case '(':
                case ')':
                case '*':
                case '+':
                case '|':
                case '<':
                case '>':
                case '&':
                    sb.append("\\\\").append(c);
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    private String processMultipartWildcardString(String var, String string, boolean startWithPercent, boolean endsWithPercent, char quoteSymbol) {
        String[] values = StringUtils.splitPreserveAllTokens(string, "%");
        //we have optimized cases for 2 and 3 part wildcard. For example hello%world or hello%w%ld. If the wildcard string will contain more symbols, should be executed universal matching function that acepts String[]args
        if (values.length == 2) {
            return "like2Parts(" + var + "," + quoteSymbol + values[0] + quoteSymbol + "," + quoteSymbol + values[1] + quoteSymbol + "," + !startWithPercent + "," + !endsWithPercent + ')';
        } else if (values.length == 3) {
            return "like3Parts(" + var + "," + quoteSymbol + values[0] + quoteSymbol + "," + quoteSymbol + values[1] + quoteSymbol + "," + quoteSymbol + values[2] + quoteSymbol + "," + !startWithPercent + "," + !endsWithPercent + ')';
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("likeNParts(").append(var).append(",");
            sb.append(!startWithPercent).append(",").append(!endsWithPercent);
            for (String v : values) {
                sb.append(",");
                sb.append(quoteSymbol).append(v).append(quoteSymbol);
            }

            sb.append(")");
            return sb.toString();
        }
    }

    private String returnWildcardFunction(String var, String toCompare, char quoteSymbol, boolean startsWithPercent, boolean endsWithPercent) {
        if (!startsWithPercent && !endsWithPercent) {
            return var + "==" + quoteSymbol + toCompare + quoteSymbol;
        }
        if (startsWithPercent && !endsWithPercent) {//'%val'
            return var + ".endsWith(" + quoteSymbol + toCompare + quoteSymbol + ")";
        }
        if (!startsWithPercent && endsWithPercent) {//'val%'
            return var + ".startsWith(" + quoteSymbol + toCompare + quoteSymbol + ")";
        }
        if (startsWithPercent && endsWithPercent) {//'%val%'
            return var + ".contains(" + quoteSymbol + toCompare + quoteSymbol + ")";
        }

        throw new RuntimeException("Not implemented");
    }
}
