package com.team.Producer.Consumer.Simulation;

import java.util.HashMap;



public class Monitor {
private static Monitor instance = null;
    private HashMap<String, Observer> observers;

    private Monitor(){
        this.observers = new HashMap<>();
    }


    public static Monitor getInstance(){
        if(instance == null){
            return new Monitor();
        }else{
            return instance;
        }
    }

    public void addObserver(String name, Observer observer) {
        this.observers.put(name,observer);
    }

    public void removeObserver(String name,Observer observer) {
        this.observers.remove(name,observer);
    }

    public void notify(String name, network network) {
        if(name.contains("M") || name.contains("Q")) {

            observers.get(name).update(network);
        }
    }
}
