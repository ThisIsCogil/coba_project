package model;

import android.graphics.Bitmap;

public class MyItem {
    private int id;
    private double price;
    private String textnama;
    private Bitmap imageItem;

    public MyItem(int id ,String textnama, double price, Bitmap imageItem) {
        this.id = id;
        this.textnama = textnama;
        this.price = price;
        this.imageItem = imageItem;
    }

    public int getId() {
        return id;
    }

    public String getTextnama() {
        return textnama;
    }

    public double getPrice() {
        return price;
    }


    public Bitmap getImageItem() {
        return imageItem;
    }
}
