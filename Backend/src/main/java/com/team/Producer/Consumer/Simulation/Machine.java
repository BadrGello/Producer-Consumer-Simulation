package com.team.Producer.Consumer.Simulation;


import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.ObjectMapper;



public class Machine {
    private  String name;
    private Vector<String> prev;
    private String next;
    private int serviceTime;
    private Product product;
    private  boolean isBusy = false;
    private Monitor monitor; 
    private Thread starting;
    private  Thread finishing;
    private final Object object = new Object();

    public Machine(String name) {
        this.prev = new Vector<String>();
        this.monitor = Monitor.getInstance();
        this.name = name;
        this.serviceTime = ThreadLocalRandom.current().nextInt(5000, 25000);
        this.isBusy = false;
        monitor.addObserver(this.name, new Observer(this.name));
    }

    public Machine Clone() {
        Machine newMachine = new Machine(this.name);
        for(String p : this.prev){
            newMachine.prev.add(p);
        }
        newMachine.next = this.next;
        newMachine.serviceTime = this.serviceTime;
        newMachine.name = this.name;
   
        return newMachine;
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
                       
                            sendUpdate("machine-flash", this.name, this.product.getColor());
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
        private synchronized void sendUpdate(String type, String machineId, String flashColor) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String message = mapper.writeValueAsString(Map.of(
                "type", type,
                "machineId", machineId,
                "flashColor", flashColor
            ));
            System.out.println("Sending WebSocket message11: " + message); 
            Controller.sendMessageToAll(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  synchronized void sendUpdate(String type, String machineId, float serviceTime) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String message = mapper.writeValueAsString(Map.of(
                "type", type,
                "machineId", machineId,
                "serveTime", serviceTime
            ));
            System.out.println("Sending WebSocket message12: " + message); 
            Controller.sendMessageToAll(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public  void work(Queue prevQueue, Queue nextQueue, network network) {
        
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

    public int getServiceTime() {
        return this.serviceTime;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    public Thread getStarting() {
        return this.starting;
    }
    public Thread getFinishing() {
        return this.finishing;
    }

}
