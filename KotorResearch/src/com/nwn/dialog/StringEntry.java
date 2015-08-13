package com.nwn.dialog;

/**
 * @author sad
 */
public class StringEntry {
    private int id;
    private String string;
    private String voiceResRef;
    private float soundLength;
    private int flag;

    public StringEntry(int id, String string, String voiceResRef, float soundLength, int flag) {
        this.id = id;
        this.string = string;
        this.voiceResRef = voiceResRef;
        this.soundLength = soundLength;
        this.flag = flag;
    }
    
}
