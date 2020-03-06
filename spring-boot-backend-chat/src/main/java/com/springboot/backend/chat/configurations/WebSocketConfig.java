package com.springboot.backend.chat.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker    // habilita el servidor/broker  de websocket en spring
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/chat-websocket") // misma ruta url para conectarnos desde angular a este servidor websocket de spring
        .setAllowedOrigins("http://localhost:4200")
                .withSockJS();  // por debajo stomp utiliza sockJs, nos permite utilizar el protocolo http para conectarnos al servidor de websocket
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/chat/");    // prefijo para los nombre de evento, cuando el servidor emite un mensaje o notifica a todos los clientes, debemos indicar el nombre del evento
        registry.setApplicationDestinationPrefixes("/app");        // prefijo de la destinacion, cuando publicamos un mensaje, cuando lo publicamos enviamos nuestro payload, nuestro objeto al broker hacia un destino, ese destin ose especifica con messageMapping y ahi colocamos el nombre del destino
    }
}
