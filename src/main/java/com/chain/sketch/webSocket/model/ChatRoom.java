package com.chain.sketch.webSocket.model;

import com.chain.sketch.advice.exception.RoomExistException;
import com.chain.sketch.advice.exception.RoomNotFoundException;
import com.chain.sketch.entity.Room;
import com.chain.sketch.repo.RoomJpaRepo;
import com.chain.sketch.utils.BeanUtil;
import com.chain.sketch.webSocket.utils.MessageSendUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
public class ChatRoom {
    private String id;
    private String name;
    private Set<WebSocketSession> sessions = new HashSet<>();
    private RoomJpaRepo roomJpaRepo;

    public ChatRoom(String name) {
        this.name = name;
        roomJpaRepo = (RoomJpaRepo) BeanUtil.getBean("roomJpaRepo");
    }

    // 채팅방 생성
    public static ChatRoom create(@NonNull String name) {
        ChatRoom room = new ChatRoom(name);
        room.id = name;
        return room;
    }

    public void handleMessage(WebSocketSession session, ChatMessage chatMessage, ObjectMapper objectMapper) {
        MessageType chatType = chatMessage.getType();
        String title = chatMessage.getChatRoomId();

        if (chatType == MessageType.JOIN) {
            join(session);
            chatMessage.setMessage(chatMessage.getWriter() + "님이 입장했습니다.");
        }

        if (chatType == MessageType.READY) {
            Room room = roomJpaRepo.findByTitle(title).orElseThrow(RoomExistException::new);
            Integer people = room.getReadyPeople() + 1;
            room.setReadyPeople(people);
            roomJpaRepo.save(room);
            chatMessage.setMessage(chatMessage.getWriter() + "님이 준비했습니다. 현재" + people + "명 준비됨.");
        }

        if (chatType == MessageType.START) {
            log.info("chatRoomId : {}", title);
            log.info("rooms : {}", roomJpaRepo.findAll());
            log.info("room : {} ", roomJpaRepo.findByTitle(title).orElseThrow(RoomNotFoundException::new));
            Room room = roomJpaRepo.findByTitle(title).orElseThrow(RoomNotFoundException::new);
            log.info("leader : {}", room.getLeaderIdx());
            if(!room.getReadyPeople().equals(room.getAllPeople())) {
                chatMessage.setType(MessageType.ERROR);
                chatMessage.setMessage("플레이어들이 모두 준비하지 않아 게임을 시작할 수 없습니다.");
            } else chatMessage.setMessage("게임이 시작되었습니다.");
        }

        send(chatMessage, objectMapper);
    }

    private void join(WebSocketSession session) {
        sessions.add(session);
    }

    private <T> void send(T messageObject, ObjectMapper objectMapper) {
        try {
            TextMessage message = new TextMessage(objectMapper.writeValueAsString(messageObject));
            sessions.parallelStream().forEach(session -> MessageSendUtils.sendMessage(session, message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void remove(WebSocketSession target) {

        String targedId = target.getId();
        sessions.removeIf(session -> session.getId().equals(targedId));
    }
}