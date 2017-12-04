package com.gooddies.regexp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dmitry
 */
public class RegExpExtractor {
    final Pattern p;
    Matcher m;

    public RegExpExtractor(String pattern) {
        this.p = Pattern.compile(pattern);
    }

    public void startMatcher(String string) {
        m = p.matcher(string);
    }

    public boolean nextMatch() {
        return m.matches();
    }

    public String[] groups() {
        String[] strs = new String[m.groupCount()];
        for (int i = 1; i <= m.groupCount(); i++) {
            strs[i - 1] = m.group(i);
        }
        return strs;
    }

    public boolean match(String string) {
        return p.matcher(string).matches();
    }

}
