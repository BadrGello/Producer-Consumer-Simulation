package com.team.Producer.Consumer.Simulation;

import java.util.concurrent.ThreadLocalRandom;

public class Input {
 private Thread inputThread;

    public void addProduct(Queue queue, network network){
        Runnable input = () -> {
            int products = ThreadLocalRandom.current().nextInt(10, 20);
            System.out.println("Products to be added: " + products);
            long rate = ThreadLocalRandom.current().nextInt(500, 1000);
            System.out.println("input rate: " + rate);
            int check = 0;
            while(!inputThread.isInterrupted()){
                synchronized (this){
                    try{
                        if(check >= products)
                            break;
                        Product product = new Product();
                        System.out.println("Product added: " + (product != null));                            
                        queue.enqueue(product, network);
                        Thread.sleep(rate);
                        check++;
                    }
                    catch (Exception e){
                        System.out.println(e);
                    }
                }
                if(network.stop){
                    this.inputThread.interrupt();
                }
            }
        };
        this.inputThread = new Thread(input);
        this.inputThread.start();
    }
}
