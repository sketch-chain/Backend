package com.chain.sketch.webSocket.config;

import com.chain.sketch.webSocket.ChatHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ChatHandler chatHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 해당 endpoint 로 handshake 가 이루어 진다.
        // SockJS 연동 시 .withSockJS() 옵션 추가
        registry.addHandler(chatHandler, "/ws/chat").setAllowedOrigins("*");
    }
}