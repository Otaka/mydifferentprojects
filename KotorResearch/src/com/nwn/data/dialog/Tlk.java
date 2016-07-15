package com.nwn.data.dialog;

/**
 * @author sad
 */
public class Tlk {

    private final int languageId;
    private final StringEntry[] entries;

    private final StringEntry emptyString;

    public Tlk(int languageId, StringEntry[] entries) {
        this.languageId = languageId;
        this.entries = entries;
        emptyString = new StringEntry(-1, "", "", 0, 0);
    }

    public int getLanguageId() {
        return languageId;
    }

    public StringEntry[] getEntries() {
        return entries;
    }

    public StringEntry getString(int stringResRef) {
        if (stringResRef < 0 || stringResRef >= entries.length) {
            return emptyString;
        }

        return entries[stringResRef];
    }
}
