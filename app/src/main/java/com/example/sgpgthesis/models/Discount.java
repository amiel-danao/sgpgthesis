package com.example.sgpgthesis.models;

public class Discount {
    private String id;
    private int min_quantity;
    private int max_quantity;
    private double percent;


    public Discount() {
    }

    public Discount(String id, int min_quantity, int max_quantity, double percent) {
        this.id = id;
        this.min_quantity = min_quantity;
        this.max_quantity = max_quantity;
        this.percent = percent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMin_quantity() {
        return min_quantity;
    }

    public void setMin_quantity(int min_quantity) {
        this.min_quantity = min_quantity;
    }

    public int getMax_quantity() {
        return max_quantity;
    }

    public void setMax_quantity(int max_quantity) {
        this.max_quantity = max_quantity;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return
                percent + "% off with " +
                "min qty of " + min_quantity +
                " and max qty of " + max_quantity;
    }
}
