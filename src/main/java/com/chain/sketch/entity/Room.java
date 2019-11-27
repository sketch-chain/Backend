package com.chain.sketch.entity;

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

    // 게임 진행 중인지 여부 : false - 대기 중 , true - 게임 진행 중
    @Column(nullable = false)
    private Boolean isPlaying;

    // 게임 진행 라운드 수 : 1, 3, 5
    @Column(nullable = false)
    private Integer round;

    // 한 턴의 제한 시간 : 60, 90, 120
    @Column(nullable = false)
    private Integer limitTime;

    // 비밀번호 추가
    @Column(nullable = true, length = 30)
    private String password;
}
