package com.latin.cwbastardi.latin;

public class Word {

    private String latin;
    private String defaultTranslation;
    private int audioResource;

    //used for phrases activity
    public Word(String latin, String defaultTranslation, int audioResource){
        this.latin = latin;
        this.defaultTranslation = defaultTranslation;
        this.audioResource = audioResource;
    }

    public String getLatin() {
        return this.latin;
    }

    public String getDefaultTranslation() {
        return this.defaultTranslation;
    }

    public int getAudioResource() { return this.audioResource; }

}
