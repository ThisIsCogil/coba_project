package model;

public class MyItem {
    private String text;
    private String textnama;
    private int imageResId;

    public MyItem(String textnama, String text, int imageResId) {
        this.textnama = textnama;
        this.text = text;
        this.imageResId = imageResId;
    }

    public String getTextnama() {
        return textnama;
    }

    public String getText() {
        return text;
    }


    public int getImageResId() {
        return imageResId;
    }
}
