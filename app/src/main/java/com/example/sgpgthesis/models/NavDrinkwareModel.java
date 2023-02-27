package com.example.sgpgthesis.models;

import java.io.Serializable;

public class NavDrinkwareModel implements Serializable {
    String id;
    String name = "";
    String description;
    String img_url;
    String type;
    String rating;
    int price;

    public NavDrinkwareModel() {
    }

    public NavDrinkwareModel(String id, String name, String description, String img_url, String type, String rating, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.img_url = img_url;
        this.type = type;
        this.rating = rating;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
