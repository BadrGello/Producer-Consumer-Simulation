package com.team.Producer.Consumer.Simulation;


import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class network {
    private boolean onChange = false;
    public boolean stop = false;

    private  ArrayList<Machine> machines;
    private  ArrayList<Queue> queues;
    private int products;
    private long rate ;
            
  

    public void setChange(boolean change){
        this.onChange = change;
    }
    public boolean getChange(){
        return this.onChange;
    }

    public ArrayList<Machine> getMachines() {
        return this.machines;
    }

    public void setMachines(ArrayList<Machine> machines) {
        this.machines = machines;
    }

    public ArrayList<Queue> getQueues() {
        return this.queues;
    }

    public void setQueues(ArrayList<Queue> newQueues) {
        this.queues = newQueues;
    }

    public network(){
        machines = new ArrayList<>();
        queues = new ArrayList<>();
        this.products = ThreadLocalRandom.current().nextInt(10, 20);
        this.rate = ThreadLocalRandom.current().nextInt(500, 1000);
    }

    public void play(){
        this.stop = false;


        try {
           

            Input inputThread = new Input(this.products, this.rate);
            inputThread.addProduct(this.queues.get(0), this);
            for(Machine m:this.machines) {
                int indexInQueues1 = Integer.parseInt(m.getNextQueue().replaceAll("[^0-9]", ""));
                Queue q1 = this.queues.get(indexInQueues1);
                for (String prevQueue: m.getPrevQueues()){
                        int indexInQueues = Integer.parseInt(prevQueue.replaceAll("[^0-9]", ""));
                        Queue q = this.queues.get(indexInQueues);
                        m.work(q,q1,this);
                    }
                }
            }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void stop(){
        this.stop = true;

    }
  
    public void clear(){
      
        this.machines = new ArrayList<>();
        this.queues = new ArrayList<>();
        this.rate = 0;
        this.products = 0;   
    }
    public long getRate(){
        return this.rate;
    }

    public void setRate(long rate){
        this.rate = rate;
    }

    public int getProducts(){
        return this.products;
    }

    public void setProducts(int products){
        this.products = products;
    }

    public ArrayList<Queue> deepCopyQueues(ArrayList<Queue> queues) {
        ArrayList<Queue> copiedQueues = new ArrayList<>();
        for (Queue queue : queues) {
            copiedQueues.add(queue.Clone());
        }
        return copiedQueues;
    }
    public ArrayList<Machine> deepCopyMachines(ArrayList<Machine> machines) {
        ArrayList<Machine> copiedMachines = new ArrayList<>();
        for (Machine machine : machines) {
            copiedMachines.add(machine.Clone());
       }
        return copiedMachines;
    }
}
