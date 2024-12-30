package com.team.Producer.Consumer.Simulation;

import java.util.ArrayList;
import java.util.List;

public class History {
    List<NetworkMemento> mementos;

    public History() {
        this.mementos = new ArrayList<>();
    }
    public void addMemento(NetworkMemento memento) {
        this.mementos.add(memento);
    }

    public NetworkMemento getMemento(int index) {
        return this.mementos.get(index);
    }

}
