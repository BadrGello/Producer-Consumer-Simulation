package com.team.Producer.Consumer.Simulation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class network {
    private boolean onChange = false;
    public boolean stop = false;

    private static ArrayList<Machine> machines;
    private static ArrayList<Queue> queues;
  

    public void flipChange(){
        this.onChange = !this.onChange;
    }
    public void setChange(boolean change){
        this.onChange = change;
    }
    public boolean getChange(){
        return this.onChange;
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public void setMachines(ArrayList<Machine> machines) {
        network.machines = machines;
    }

    public ArrayList<Queue> getQueues() {
        return queues;
    }

    public void setQueues(ArrayList<Queue> newQueues) {
        queues = newQueues;
    }

    public network(){
        machines = new ArrayList<>();
        queues = new ArrayList<>();
    }

    public void play(){
        this.stop = false;


        try {
           

            Input inputThread = new Input();
            inputThread.addProduct(queues.get(0), this);
            for(Machine m:machines) {
                int indexInQueues1 = Integer.parseInt(m.getNextQueue().replaceAll("[^0-9]", ""));
                Queue q1 = queues.get(indexInQueues1);
                for (String prevQueue: m.getPrevQueues()){
                        int indexInQueues = Integer.parseInt(prevQueue.replaceAll("[^0-9]", ""));
                        Queue q = queues.get(indexInQueues);
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
      
        machines = new ArrayList<>();
        queues = new ArrayList<>();
    }

    public Map<String, Object> getNetwork(){
        ArrayList<Machine> machiness = this.getMachines();
        ArrayList<Queue> queuess = this.getQueues(); 
        Map<String, Object> res = new HashMap<>();

        for(Machine m : machiness){
            res.put(m.getName(), m);
        }
        for(Queue q: queuess){
            res.put(q.getQueueName(), q);
        }
        return res;
    }

    public NetworkMemento createMemento(){
        return new NetworkMemento(this);
    }

    public void restore(NetworkMemento memento){
        this.setMachines(memento.getNetwork().getMachines());
        this.setQueues(memento.getNetwork().getQueues());
    }
    
}
