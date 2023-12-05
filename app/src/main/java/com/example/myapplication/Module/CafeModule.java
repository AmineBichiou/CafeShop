package com.example.myapplication.Module;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CafeModule {

    private String key;
    private String name;
    private double price;
    private String img;

    public CafeModule() {
    }

public CafeModule(String name, double price, String img) {
        this.name = name;
        this.price = price;
        this.img = img;
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

