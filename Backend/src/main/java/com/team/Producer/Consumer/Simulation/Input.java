package com.team.Producer.Consumer.Simulation;

import java.util.concurrent.ThreadLocalRandom;

public class Input {
 private Thread inputThread;

    public void addProduct(Queue queue, network network){
        Runnable input = () -> {
            int products = ThreadLocalRandom.current().nextInt(5, 10);
            System.out.println("Products to be added: " + products);
            long rate = ThreadLocalRandom.current().nextInt(3000, 10000);
            System.out.println("input rate: " + rate);
            int check = 0;
            while(!inputThread.isInterrupted()){
                synchronized (this){
                    try{
                        if(check > products)
                            break;                        
                        queue.getProducts().add(new Product());
                        Thread.sleep(rate);
                        check++;
                    }
                    catch (Exception e){
                        System.out.println();
                    }
                }
                if(network.inputStop){
                    this.inputThread.interrupt();
                }
            }
        };
        this.inputThread = new Thread(input);
        this.inputThread.start();
    }
}
