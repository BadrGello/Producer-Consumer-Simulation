package com.team.Producer.Consumer.Simulation;



public class Input {
    private Thread inputThread;
    private long rate;
    private Product product = new Product();

    public Input(long rate) {
        product = new Product();      
        this.rate = rate;
    }


    public void addProduct(Queue queue, network network){
        Runnable input = () -> {
            System.out.println("rate: " + this.rate);    
            int i = 0;
            while(!inputThread.isInterrupted()){
                synchronized (this){
                    try{
                        if(!network.replayed){
                            product = new Product();
                            network.addProduct(product.Clone());
                        }
                        else{
                            if(i == network.getProducts().size()){
                                this.inputThread.interrupt();
                            }
                            product = network.getProducts().get(i++);
                           
                        }
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
