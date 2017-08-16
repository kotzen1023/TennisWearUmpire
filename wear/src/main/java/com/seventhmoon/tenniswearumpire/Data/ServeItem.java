package com.seventhmoon.tenniswearumpire.Data;


public class ServeItem {
    private int color_r;
    private int color_g;
    private int color_b;
    private String text;

    public ServeItem(int color_r, int color_g, int color_b, String text){
        this.color_r = color_r;
        this.color_g = color_g;
        this.color_b = color_b;
        this.text = text;
    }

    public int getColorR() {
        return color_r;
    }

    public int getColorG() {
        return color_g;
    }

    public int getColorB() {
        return color_b;
    }

    public String getText() {
        return text;
    }


}
