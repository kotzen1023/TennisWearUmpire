package com.seventhmoon.tenniswearumpire.Data;



public class ListenChooseItem implements Comparable<ListenChooseItem> {
    private String fileName;
    //private String date;


    public ListenChooseItem(String n)
    {
        super();
        this.fileName = n;
        //this.date = date;
    }

    public String getFileName()
    {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    //public String getDate() {
    //    return date;
    //}

    //public void setDate(String date) {
    //    this.date = date;
    //}

    @Override
    public int compareTo(ListenChooseItem o) {
        if(this.fileName != null)
            return this.fileName.toLowerCase().compareTo(o.getFileName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}

