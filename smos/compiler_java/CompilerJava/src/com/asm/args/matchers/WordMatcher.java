package com.asm.args.matchers;

import com.asm.args.argresult.AbstractParsingResult;
import com.asm.args.argresult.OkResult;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sad
 */
public class WordMatcher extends AbstractMatcher {

    private final Set<String> wordsToMatch = new HashSet<String>();

    public WordMatcher(String... words) {
        for (String word : words) {
            word = word.toLowerCase();
            wordsToMatch.add(word);
        }
    }

    @Override
    public AbstractParsingResult match(String value) {
        value = value.toLowerCase();
        if (wordsToMatch.contains(value)) {
            return new OkResult(value);
        }

        return null;
    }
}
