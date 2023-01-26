package com.example.sgpgthesis.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrderModel implements Serializable {
    List<HashMap<String, Object>> items;
    String documentId;
    double totalPrice;
    String status;
    @ServerTimestamp
    Date order_date;

    public OrderModel() {
    }

    public String getStatus() {
        return status;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<HashMap<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<HashMap<String, Object>> items) {
        this.items = items;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
