package com.chain.sketch.webSocket.repo;

import com.chain.sketch.webSocket.model.ChatRoom;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class ChatRoomRepository {
    @Getter
    private final HashMap<String, ChatRoom> chatRoomHashMap;

    public ChatRoomRepository() {
        chatRoomHashMap = new HashMap<>();
    }

    public void addChatRoom(ChatRoom chatRoom) {
        chatRoomHashMap.put(chatRoom.getId(), chatRoom);
    }

    public ChatRoom getChatRoom(String id) {
        return chatRoomHashMap.get(id);
    }

    public Set<String> getChatRooms() {
        return chatRoomHashMap.keySet();
    }

    public void remove(WebSocketSession session) {
        chatRoomHashMap.forEach((s, chatRoom) -> {
            chatRoom.remove(session);
        });
    }
}
