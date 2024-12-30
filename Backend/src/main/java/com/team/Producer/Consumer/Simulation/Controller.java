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
    public ArrayList<Machine> machines = new ArrayList<>();
    public ArrayList<Queue> queues = new ArrayList<>();
    public network Network;
    public History history = new History();
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @PostMapping("/run")
    public String run(@RequestBody Map<String, Object> requestBody) {
        Map<String, ArrayList<String>> myNetwork = (Map<String, ArrayList<String>>) requestBody.get("myNetwork");
        if (myNetwork.isEmpty()) {
            System.out.println("empty network!");
            return "empty network!";
        }
        for (String key : myNetwork.keySet()) {
            if (key.contains("M")) {
                Machine m = new Machine(key);
                m.setNextQueue(myNetwork.get(key).get(myNetwork.get(key).size() - 1));
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

        try {
            Network.play();
            return "run successfully!";
        } catch (Exception e) {
            System.out.println("Error in run!");
            return "error in run!";
        }
    }

    public static void sendMessageToAll(String message) {
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