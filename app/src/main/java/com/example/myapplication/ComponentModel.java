package com.example.myapplication;

public class ComponentModel {
    private String text,name, pnumber;

    private int textColorResId;
    private int backgroundColorResId;

    public ComponentModel(String text, int textColorResId, int backgroundColorResId) {
        this.text = text;
        this.textColorResId = textColorResId;
        this.backgroundColorResId = backgroundColorResId;
    }

    public ComponentModel(String text, int textColorResId, int backgroundColorResId, String name, String pnumber) {
        this.text = text;
        this.name = name;
        this.pnumber = pnumber;
        this.textColorResId = textColorResId;
        this.backgroundColorResId = backgroundColorResId;
    }

    public String getText() {
        return text;
    }

    public int getTextColorResId() {
        return textColorResId;
    }

    public int getBackgroundColorResId() {
        return backgroundColorResId;
    }

    public String getName(){
        return name;
    }

    public String getPnumber() {return  pnumber;}
}
