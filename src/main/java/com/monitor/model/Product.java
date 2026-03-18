package com.monitor.model;

public class Product {

    private String name;
    private String price;
    private String link;
    private String image;
    private String id;
    private double priceValue;

    public Product(String name, String price, String link, String image, String id) {

        this.name = name;
        this.price = price;
        this.link = link;
        this.image = image;
        this.id = id;

        String numeric = price
                .replace("R$", "")
                .replace(".", "")
                .replace(",", ".");

        this.priceValue = Double.parseDouble(numeric);
    }

    public String getName(){ return name; }
    public String getPrice(){ return price; }
    public String getLink(){ return link; }
    public String getImage(){ return image; }
    public String getId(){ return id; }
    public double getPriceValue(){ return priceValue; }
}
