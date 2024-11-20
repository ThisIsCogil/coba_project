package model;

public class HistoryItem {

    private String textnama;
    private String textharga2;
    private String texttgl;
    private int imageResId;

    public HistoryItem(String textnama, String texttgl, String textharga2, int imageResId) {
        this.textnama = textnama;
        this.texttgl = texttgl;
        this.textharga2 = textharga2;
        this.imageResId = imageResId;
    }

    public String getTextnama() {
        return textnama;
    }

    public String getTextharga() {
        return textharga2;
    }

    public String getTexttgl() {
        return texttgl;
    }

    public int getImageResId() {
        return imageResId;
    }
}


