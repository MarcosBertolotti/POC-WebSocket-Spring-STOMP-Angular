package com.springboot.backend.chat.controllers;

import static com.springboot.backend.chat.util.Color.getColors;
import static com.springboot.backend.chat.util.WebSocketConstants.NEW_USER;

import com.springboot.backend.chat.models.documents.Message;
import com.springboot.backend.chat.services.IChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
public class ChatController {

    @Autowired
    private IChatService chatService;

    @Autowired
    private SimpMessagingTemplate webSocket;

    private List<String> colors = new ArrayList<>(getColors());

    @MessageMapping("/message")     // indicamos destino/nombre donde los clientes van a enviar los mensajes del chat. con prefijo app
    @SendTo("/chat/message")        // indicamos el nombre del evento al cual el broker va a enviar el mensaje, a los usuarios subscritptos al evento
    public Message receiveMessage(Message message) {  // recibe mensaje de los clientes cuando realizan el send/publish

        message.setDate(new Date().getTime());  // con milisegundos

        if(message.getType().equals(NEW_USER)){
            message.setColor(colors.get(new Random().nextInt(colors.size())));
            message.setText("new user");
        } else {
            chatService.save(message);
        }

        return message;
    }

    @MessageMapping("/writing") // destino, notifica al servidor cuando alguien escribe en el chat
    @SendTo("/chat/writing")    // notifica al resto de los usuarios conectados
    public Map isTyping(String username) {

        Map<String, Object> response = new HashMap<>();
        response.put("writing", username + " is typing ...'");
        response.put("username", username);

        return response;
    }

    @MessageMapping("/history") // notificar al broker para que nos envie el historial
    public void history(String clientId) {          // subscripcion a este evento sera por cada sesion, individual, cada cliente va a tener su propia subscripcion al chat historial, el cual va a ser unico para ese cliente

        webSocket.convertAndSend("/chat/history/" + clientId, chatService.getLast25Messages()); // destino (nombre evento) y objeto a enviar
    }


}
