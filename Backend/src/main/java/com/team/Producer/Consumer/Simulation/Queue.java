package com.team.Producer.Consumer.Simulation;

import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;


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
            sendUpdate("queue-update", this.name, this.products.size());
        }
    }

    public Product dequeue(network network) throws Exception{
        synchronized (this){
            while(this.products.isEmpty()) this.wait();
            this.monitor.notify(this.name, network);
            sendUpdate("queue-update", this.name, this.products.size()-1);
            return this.products.remove(0);
        }
    }
        private void sendUpdate(String type, String queueId, int count) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String message = mapper.writeValueAsString(Map.of(
                "type", type,
                "queueId", queueId,
                "count", count
            ));
            System.out.println("Sending WebSocket message: " + message); // Add this line
            Controller.sendMessageToAll(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
