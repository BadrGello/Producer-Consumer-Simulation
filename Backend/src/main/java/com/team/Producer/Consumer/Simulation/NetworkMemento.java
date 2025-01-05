package com.team.Producer.Consumer.Simulation;



public class NetworkMemento {
    private network network = new network();
  
    public NetworkMemento(network networky) {
         
        this.network.setMachines(networky.deepCopyMachines(networky.getMachines()));
        this.network.setQueues(networky.deepCopyQueues(networky.getQueues()));
        this.network.setProducts(networky.deepCopyProducts(networky.getProducts()));
        for(Product p: network.getProducts()){
            System.out.println("P: " + p.getColor());
        }
        for(Machine m: network.getMachines()){
            System.out.println("m: " + m.getServiceTime());
        }
        this.network.replayed = networky.replayed;  
        this.network.setRate(networky.getRate());

    }

    public network getNetwork() {
        System.out.println("ret "+this.network.getQueues().size());
        return this.network;
    }
}
