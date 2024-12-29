package com.team.Producer.Consumer.Simulation;

public class Observer {
   

    public Observer(String name) {
    }

    public void update(network network){    
        network.setChange(true);
    }
    
}
