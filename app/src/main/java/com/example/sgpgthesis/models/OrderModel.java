package com.example.sgpgthesis.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrderModel implements Parcelable {
    List<HashMap<String, Object>> items;
    String documentId;
    double totalPrice;
    String status;
    @ServerTimestamp
    Date order_date;

    public OrderModel() {
    }

    protected OrderModel(Parcel in) {
        documentId = in.readString();
        totalPrice = in.readDouble();
        status = in.readString();
    }

    public static final Creator<OrderModel> CREATOR = new Creator<OrderModel>() {
        @Override
        public OrderModel createFromParcel(Parcel in) {
            return new OrderModel(in);
        }

        @Override
        public OrderModel[] newArray(int size) {
            return new OrderModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(documentId);
        parcel.writeDouble(totalPrice);
        parcel.writeString(status);
    }
}
