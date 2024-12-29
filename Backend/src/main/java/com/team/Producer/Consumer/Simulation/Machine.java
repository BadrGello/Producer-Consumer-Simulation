package com.team.Producer.Consumer.Simulation;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;



public class Machine {
    private  String name;
    private Vector<String> prev;
    private String next;
    private long serviceTime;
    private Product product;
    private String color;
    private  boolean isBusy = false;
    private Monitor monitor; 
    private Thread starting;
    private  Thread finishing;
    private final Object object = new Object();

    public Machine(String name) {
        this.monitor = Monitor.getInstance();
        this.name = name;
        this.serviceTime = ThreadLocalRandom.current().nextLong(5000, 25000);
        this.isBusy = false;
        monitor.addObserver(this.name, new Observer(this.name));
    }

    

    
    private void start(Queue prevQueue, network network) {
        while (!starting.isInterrupted()) {
            synchronized (object) {
                try {
                    while (prevQueue.getProducts().isEmpty()) {
                        monitor.notify(this.name, network);
                        object.wait();
                    }
                    this.setProduct(prevQueue.dequeue(network)); 
                    System.out.println(this.getName() +" "+this.product.getColor() + " " + this.serviceTime);
                    monitor.notify(this.name, network); 
                    isBusy = true; 
                    object.wait(); 
                    object.notifyAll(); 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (network.stop) {
                this.starting.interrupt();
            }
        }
    }
    
        
    private void finish(Queue prevQueue, Queue nextQueue, network network) {
        while (!finishing.isInterrupted()) {
            synchronized (object) {
                try {
                    if (!prevQueue.getProducts().isEmpty() && !isBusy) {
                        object.notifyAll(); 
                    }
                    while (isBusy && product != null) {
                        Thread.sleep(this.serviceTime);
                        nextQueue.enqueue(product, network);
                        System.out.println(nextQueue.getQueueName() + " " + this.product.getColor());
                        object.notifyAll();
                        this.setProduct(null); 
                        isBusy = false; 
                        object.wait(); 
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (network.stop) {
                this.finishing.interrupt();
            }
        }
    }

    
    public void work(Queue prevQueue, Queue nextQueue, network network) {
        
        this.starting = new Thread(() -> start(prevQueue, network));
        
        this.finishing = new Thread(() -> finish(prevQueue, nextQueue, network));

        
        starting.start();
        finishing.start();
    }
        
    public List<String> getPrevQueues(){
        return this.prev;
    }
    public void setPrevQueues(Vector<String> prev){
        this.prev = prev;
    }
    public String getNextQueue(){
        return this.next;
    }
    public void setNextQueue(String nextQueue){
        this.next = nextQueue;
    }       

    public String getName() {
        return this.name;
    }

    public long getServiceTime() {
        return this.serviceTime;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if(product != null)
            this.color = product.getColor();
    }


}
