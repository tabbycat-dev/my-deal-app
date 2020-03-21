package com.example.mydeal;

import android.graphics.Bitmap;

public class Item {
    String id;
    String price;
    String wasPrice;
    String name;
    String url;
    String image;
    boolean priceOff;
    String percent;
    String save;
    Bitmap imageBitmap;

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Item(String url) {
        this.url = url;
    }
    public void updateItem(String id,String image,String price,String wasPrice, String name, String url) {
        this.id = id;
        this.image = image;
        this.url = url;
        this.price = price;
        this.name = name;
        this.wasPrice = wasPrice;
        if(!this.wasPrice.equals("$0.00")){
            this.priceOff =true;
            this.save = this.calculateSave(price,wasPrice);
            this.percent = this.calculatePercent(this.save, wasPrice);
        }else {
            this.priceOff = false;
            this.percent = null;
            this.save = null;
        }
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public boolean isPriceOff() {
        return priceOff;
    }

    public void setPriceOff(boolean priceOff) {
        this.priceOff = priceOff;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWasPrice() {
        return wasPrice;
    }

    public void setWasPrice(String wasPrice) {
        this.wasPrice = wasPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String calculatePercent(String price, String wasPrice ){
        String x = price.substring(1);
        String y = wasPrice.substring(1);
        return String.format("%.2f",((Double.parseDouble(x)/Double.parseDouble(y))*100))+"%";
    }
    public String calculateSave(String price, String wasPrice ){
        String x = price.substring(1);
        String y = wasPrice.substring(1);
        return "$"+String.format("%.2f",(Double.parseDouble(y)-Double.parseDouble(x)));
    }
}
