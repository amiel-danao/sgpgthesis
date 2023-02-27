package com.example.sgpgthesis.models;

import java.io.Serializable;

public class CartModel implements Serializable {
    String productName;
    String productDescription = "";
    String productPrice;
    String currentDate;
    String currentTime;
    String totalQuantity;
    String image;
    String productImage = "";
    String documentId;
    float origPrice;
    float totalPrice;

    public CartModel() {
    }

    public CartModel(String productName, String productDescription, String productPrice, String currentDate, String currentTime, String totalQuantity, String image, String productImage, String documentId, float origPrice, float totalPrice) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.totalQuantity = totalQuantity;
        this.image = image;
        this.productImage = productImage;
        this.documentId = documentId;
        this.origPrice = origPrice;
        this.totalPrice = totalPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public float getOrigPrice() {
        return origPrice;
    }

    public void setOrigPrice(float origPrice) {
        this.origPrice = origPrice;
    }
}
