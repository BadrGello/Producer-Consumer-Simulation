package com.team.Producer.Consumer.Simulation;

public class Product {
    private String color ;
    public Product(){
        this.color = randomColor.generate();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Product Clone(){
        Product copiedProduct = new Product();
        copiedProduct.color = this.color;
        return copiedProduct;
    }
}
    