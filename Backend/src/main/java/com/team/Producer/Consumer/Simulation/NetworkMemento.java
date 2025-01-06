package com.team.Producer.Consumer.Simulation;



public class NetworkMemento {
    private network network = new network();
  
    public NetworkMemento(network networky) {
         
        this.network.setMachines(networky.deepCopyMachines(networky.getMachines()));
        this.network.setQueues(networky.deepCopyQueues(networky.getQueues()));
        this.network.setProducts(networky.deepCopyProducts(networky.getProducts()));
        this.network.replayed = networky.replayed;  
        this.network.setRate(networky.getRate());

    }

    public network getNetwork() {
        return this.network;
    }
}
