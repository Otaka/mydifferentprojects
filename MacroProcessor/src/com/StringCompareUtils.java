package com;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sad
 */
public class StringCompareUtils {

    public static boolean like2Parts(String value, String firstPart, String secondPart, boolean startsWith, boolean endsWith) {
        int index = 0;
        if (firstPart.length() + secondPart.length() > value.length()) {
            return false;
        }

        if (startsWith) {
            if (!value.startsWith(firstPart)) {
                return false;
            }
            index = firstPart.length();
        } else {
            index = value.indexOf(firstPart);
            if (index == -1) {
                return false;
            }
            index += firstPart.length();
        }

        if (endsWith) {
            if (!value.endsWith(secondPart)) {
                return false;
            }
        } else {
            if (value.indexOf(secondPart, index) == -1) {
                return false;
            }
        }
        return true;
    }

    public static boolean like3Parts(String value, String firstPart, String secondPart, String thirdPart, boolean startsWith, boolean endsWith) {
        int index = 0;
        if (firstPart.length() + secondPart.length() + thirdPart.length() > value.length()) {
            return false;
        }

        if (startsWith) {
            if (!value.startsWith(firstPart)) {
                return false;
            }
            index = firstPart.length();
        } else {
            index = value.indexOf(firstPart);
            if (index == -1) {
                return false;
            }
        }

        index = value.indexOf(secondPart);
        if (index == -1) {
            return false;
        }
        index += secondPart.length();

        if (endsWith) {
            if (index + thirdPart.length() > value.length()) {
                return false;
            }
            if (!value.endsWith(thirdPart)) {
                return false;
            }
        } else {
            if (value.indexOf(thirdPart, index) == -1) {
                return false;
            }
        }
        return true;
    }

    public static boolean likeNParts(String value, boolean startsWith, boolean endsWith, String... parts) {
        int indexInString = 0;
        int matchPart = 0;
        if (startsWith) {
            if (!value.startsWith(parts[0])) {
                return false;
            }

            indexInString = parts[0].length();
            matchPart++;
        }

        int partsToMatch = parts.length;
        if (endsWith) {
            partsToMatch--;//we should not match last part in cycle, because it should be checked for contains' in current loop, it will be checked in next step
        }

        for (; matchPart < partsToMatch; matchPart++) {
            String part = parts[matchPart];
            indexInString = value.indexOf(part, indexInString);
            if (indexInString == -1) {
                return false;
            }
            indexInString += part.length();
        }

        if (endsWith) {
            String lastPart = parts[parts.length - 1];
            if (indexInString + lastPart.length() > value.length()) {
                return false;
            }

            if (!value.endsWith(lastPart)) {
                return false;
            }
        }
        return true;
    }

    private static final Map<String, Matcher> patterns = new HashMap<String, Matcher>();
    public static boolean likeRegexp(String value, String patternString) {
        Matcher matcher = patterns.get(patternString);
        if (matcher == null) {
            Pattern pattern = Pattern.compile(patternString);
            matcher = pattern.matcher("");
            patterns.put(patternString, matcher);
        }

        matcher.reset(value);
        return matcher.find();
    }
}