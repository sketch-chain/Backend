package com.chain.sketch.webSocket;

import com.chain.sketch.webSocket.model.ChatMessage;
import com.chain.sketch.webSocket.model.ChatRoom;
import com.chain.sketch.webSocket.repo.ChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class ChatHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatRoomRepository repository;

    @Autowired
    public ChatHandler(ObjectMapper objectMapper, ChatRoomRepository chatRoomRepository) {
        this.objectMapper = objectMapper;
        this.repository = chatRoomRepository;
    }

    // session 에서 메시지를 수신했을 때 실행된다.
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();
        log.info("payload : {}", payload);
//        log.info("session : {}", session.getId());

        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
//        log.info("chatRoomId : {}", chatMessage.getChatRoomId());
//        log.info("roomId : {}", repository.getChatRoom(chatMessage.getChatRoomId()));
//        log.info("rooms : {}", repository.getChatRooms());
        ChatRoom chatRoom = repository.getChatRoom(chatMessage.getChatRoomId());
        chatRoom.handleMessage(session, chatMessage, objectMapper);
    }

    // connection 이 close 된 후에 실행된다.
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        repository.remove(session);
    }
}

