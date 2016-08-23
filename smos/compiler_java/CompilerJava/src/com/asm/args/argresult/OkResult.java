package com.asm.args.argresult;

/**
 * @author sad
 */
public class OkResult extends AbstractParsingResult {

    private String matched;

    public OkResult() {
    }

    public OkResult(String matched) {
        this.matched = matched;
    }

    public String getMatched() {
        return matched;
    }

}
