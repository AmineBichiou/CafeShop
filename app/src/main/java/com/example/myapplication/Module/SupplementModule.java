package com.example.myapplication.Module;

public class SupplementModule {
    private String key;
    private String name;
    private double price;
    private String img;

    public SupplementModule() {
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public void setImg(String img) {
        this.img = img;
    }
}
