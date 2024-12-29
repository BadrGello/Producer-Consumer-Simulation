package com.team.Producer.Consumer.Simulation;
import java.util.ArrayList;
import java.util.Vector;

public class Test {
    public static void main(String[] args) {
        Machine machine1 = new Machine("M1");
        Machine machine2 = new Machine("M2");
        Machine machine3 = new Machine("M3");

        Queue queue0 = new Queue("Q0");
        Queue queue1 = new Queue("Q1");
        Queue queue2 = new Queue("Q2");
        
        Vector<String> prevQueues = new Vector<>();
        prevQueues.add("Q0");
        Vector<String> prevQueues1 = new Vector<>();
        prevQueues1.add("Q1");
        Vector<String> prevQueues2 = new Vector<>();
        prevQueues2.add("Q2");

        machine1.setPrevQueues(prevQueues);
        machine1.setNextQueue("Q1");

        machine2.setPrevQueues(prevQueues);
        machine2.setNextQueue("Q1");

        machine3.setPrevQueues(prevQueues1);
        machine3.setNextQueue("Q2");

        ArrayList<Queue> queues = new ArrayList<>();    
        ArrayList<Machine> machines = new ArrayList<>();
        queues.add(queue0);
        queues.add(queue1);
        queues.add(queue2);
        machines.add(machine1);
        machines.add(machine2);
        machines.add(machine3);

        network network = new network();
        network.setMachines(machines);
        network.setQueues(queues);
        network.play();

    }
}
//              ---> M1 ---|
//         Q0 -|            -----> Q1 ----> M3 ----> Q2 
//              ---> M2----|
//