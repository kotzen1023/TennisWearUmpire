package com.seventhmoon.tenniswearumpire.Data;


import android.graphics.Bitmap;

import com.seventhmoon.tenniswearumpire.util.Purchase;

public class ImageBuyItem {
    private Bitmap image;
    private String title;
    private String path;
    private Purchase purchase;
    private boolean selected;
    private boolean purchased;

    public ImageBuyItem(Bitmap image, String title) {
        super();
        this.image = image;
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }

    public boolean getSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public boolean getPurchased()
    {
        return purchased;
    }

    public void setPurchased(boolean purchased)
    {
        this.purchased = purchased;
    }
}
