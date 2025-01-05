package com.team.Producer.Consumer.Simulation;



public class Input {
    private Thread inputThread;
    private long rate;

    public Input(long rate) {      
        this.rate = rate;
    }


    public void addProduct(Queue queue, network network){
        Runnable input = () -> {
            System.out.println("rate: " + this.rate);    
            while(!inputThread.isInterrupted()){
                synchronized (this){
                    try{
                        Product product = new Product();
                        System.out.println("Product added: " + (product != null));                            
                        queue.enqueue(product, network);
                        Thread.sleep(rate);
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
