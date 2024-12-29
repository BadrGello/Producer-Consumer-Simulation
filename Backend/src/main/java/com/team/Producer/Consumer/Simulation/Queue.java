package com.team.Producer.Consumer.Simulation;

import java.util.ArrayList;


public class Queue {
    String name;
    private ArrayList<Product> products;
    private Monitor monitor;
    int size;

    public Queue(String name){
        this.products = new ArrayList<>();
        this.name = name;
        this.monitor = Monitor.getInstance();
        this.monitor.addObserver(this.name, new Observer(this.name));
    }

    public synchronized ArrayList<Product> getProducts() {
        synchronized (this){
            return this.products;
        }
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getQueueName() {
        return this.name;
    }

    public int getSize() {
        return this.products.size();
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setQueueName(String name) {
        this.name = name;
    }

    public Queue Clone(){
        Queue newQueue = new Queue(this.name);
        for(Product p : this.products){
            newQueue.products.add(p.Clone());
        }
        newQueue.size = this.size;
        return newQueue;
    }

    public synchronized void enqueue(Product product, network network) throws Exception{
        synchronized (this){
            this.products.add(product);
            this.monitor.notify(this.name, network);
            this.notify();
        }
    }

    public Product dequeue(network network) throws Exception{
        synchronized (this){
            while(this.products.isEmpty()) this.wait();
            this.monitor.notify(this.name, network);
            return this.products.remove(0);
        }
    }

}
