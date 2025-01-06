package com.team.Producer.Consumer.Simulation;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.context.annotation.Configuration;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(path = "")
public class Controller {
    
    public static network Network;
    public static History history = new History();
    public static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @PostMapping("/run")
    public String run(@RequestBody Map<String, Object> requestBody) {
        ArrayList<Machine> machines = new ArrayList<>();
        ArrayList<Queue> queues = new ArrayList<>();
        Map<String, ArrayList<String>> myNetwork = (Map<String, ArrayList<String>>) requestBody.get("myNetwork");
        if (myNetwork.isEmpty()) {
            System.out.println("empty network!");
            return "empty network!";
        }
        for (String key : myNetwork.keySet()) {
            if (key.contains("M")) {
                Machine m = new Machine(key);
                m.setNextQueue(myNetwork.get(key).get(myNetwork.get(key).size()-1));
                Vector<String> prevQs = new Vector<>();
                for (int i = 0; i < myNetwork.get(key).size()-1; i++) {
                    prevQs.add(myNetwork.get(key).get(i));
                }
                m.setPrevQueues(prevQs);
                System.out.println(m.getNextQueue());
                machines.add(m);
                
            } else {
                Queue q = new Queue(key);
                queues.add(q);
            }
        }
        Network = new network();
        Network.setMachines(machines);
        Network.setQueues(queues);
        for(Machine m: Network.getMachines()){
            System.out.println("m: " + m.getServiceTime());
            m.sendUpdate("machine-update", m.getName(), ((float) m.getServiceTime()/1000));
        }

        try {
            Network.play();
            return "run successfully!";
        } catch (Exception e) {
            System.out.println("Error in run!");
            return "error in run!";
        }
    }
    @PostMapping("/clear")
    public String clear(@RequestBody String requestBody)  {
        try {
            if(!Network.stop){
                history.addMemento(new NetworkMemento(Network));
            }
            Network.stop(); 
            Network.clear();
  
            return "clear successfully!";
        } catch (Exception e) {
            System.out.println("Error in clear!");
            return "error in clear!";       
        }
    }   

    @PostMapping("/stop")
    public String stop(@RequestBody String requestBody)  {
        try {
            if(!Network.stop){
                history.addMemento(new NetworkMemento(Network));
            }
            Network.stop(); 
            Network.clear();
            return "stop successfully!";
        } catch (Exception e) {
            System.out.println("Error in stop!");
            return "error in stop!";       
        }
    }  

    @PostMapping("/replay")
    public String replay(@RequestBody String requestBody)  {
        try {
        
            network networky = new network();
            networky = history.getMemento(0).getNetwork();
            
            Controller.Network.setMachines(networky.deepCopyMachines(networky.getMachines()));
            Controller.Network.setQueues(networky.deepCopyQueues(networky.getQueues()));
            System.out.println("sizeQ: " + networky.getQueues().size());
            Controller.Network.setProducts(networky.deepCopyProducts(networky.getProducts()));
            System.out.println("sizeP: " + networky.getProducts().size());
            Controller.Network.replayed = true; 
            Controller.Network.setRate(networky.getRate());

            for(Machine m: Network.getMachines()){
                System.out.println("m: " + m.getServiceTime());
                m.sendUpdate("machine-update", m.getName(), ((float) m.getServiceTime()/1000));
            }

            System.out.println("rate: " + Network.getRate());
            //history.addMemento(new NetworkMemento(Network));
            Network.play(); 
            return "replayed successfully!";
        } catch (Exception e) {
            System.out.println("Error in replay!");
            return "error in replay!";       
        }
    }  

    public synchronized static void sendMessageToAll(String message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    

    @Configuration
    @EnableWebSocket
    public class WebSocketConfig implements WebSocketConfigurer {
        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry.addHandler(new WebSocketHandler(), "/ws").setAllowedOrigins("*");
        }
    }

    public class WebSocketHandler extends TextWebSocketHandler {
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessions.add(session);
            System.out.println("WebSocket connection established: " + session.getId());
        }

        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            System.out.println("Received WebSocket message: " + message.getPayload());
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            sessions.remove(session);
            System.out.println("WebSocket connection closed: " + session.getId());

        }
    }
}