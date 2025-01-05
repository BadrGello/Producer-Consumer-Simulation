package com.team.Producer.Consumer.Simulation;



public class NetworkMemento {
    private network network = new network();
  
    public NetworkMemento(network networky) {
         
        this.network.setMachines(networky.deepCopyMachines(networky.getMachines()));
        this.network.setQueues(networky.deepCopyQueues(networky.getQueues()));
        System.out.println("gettt "+this.network.getQueues().size());
        this.network.setRate(networky.getRate());

    }

    public network getNetwork() {
        System.out.println("ret "+this.network.getQueues().size());
        return this.network;
    }
}
