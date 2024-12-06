package models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Product {
    @SerializedName("code")
    private int code;

    @SerializedName("name")
    private String name;

    @SerializedName("barcodes")
    private List<String> barcodes;

    @SerializedName("sellPricePerUnit")
    private Double price;

//    public Product(String name, List<String> Barcodes, Double price) {
//        this.name = name;
//        this.barcodes = Barcodes;
//        this.price = price;
//    }

    public List<String> getBarcodes() {
        return this.barcodes;
    }

    public Double getPrice() {
        return this.price;
    }

    public String getName() {
        return this.name;
    }

    public int getCode() {
        return this.code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
