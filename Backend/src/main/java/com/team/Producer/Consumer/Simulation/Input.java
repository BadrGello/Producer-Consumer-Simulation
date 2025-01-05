package com.team.Producer.Consumer.Simulation;



public class Input {
    private Thread inputThread;
    private int products;
    private long rate;

    public Input( int products, long rate) {      
        this.products = products;
        this.rate = rate;
    }


    public void addProduct(Queue queue, network network){
        Runnable input = () -> {
            System.out.println("rate: " + this.rate);    
            System.out.println("products: " + this.products);
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
