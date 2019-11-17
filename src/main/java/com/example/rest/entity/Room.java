package com.example.rest.entity;

import io.swagger.models.auth.In;
import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, length = 30, unique = true)
    private String title;

    // 방장 idx
    @Column(nullable = false, length = 30)
    private String leaderIdx;

    // 게임에 참가중인 사람수
    @Column(nullable = false)
    private Integer allPeople;

    // 게임 준비한 사람수
    @Column(nullable = false)
    private Integer readyPeople;

    // 게임이 비밀방인지 여부
    @Column(nullable = false)
    private Boolean isSecret;

    // 비밀번호 추가
}
