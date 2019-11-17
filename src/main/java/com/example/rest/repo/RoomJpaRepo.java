package com.example.rest.repo;

import com.example.rest.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomJpaRepo extends JpaRepository<Room, Long> {
    Optional<Room> findByIdx(Long idx);
    Optional<Room> findByTitle(String title);
}
