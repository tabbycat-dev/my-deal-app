package com.example.mydeal;

public class Item {
    String price;
    String name;
    String url;
    String image;

    public Item(String url) {
        this.url = url;
    }
    public void updateItem(String price, String name, String url) {
        this.url = url;
        this.price = price;
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }
}
