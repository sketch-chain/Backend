package com.chain.sketch.controller.v1;


import com.chain.sketch.advice.exception.RoomExistException;
import com.chain.sketch.advice.exception.RoomNotFoundException;
import com.chain.sketch.advice.exception.UserNotFoundException;
import com.chain.sketch.entity.Room;
import com.chain.sketch.entity.User;
import com.chain.sketch.model.response.CommonResult;
import com.chain.sketch.model.response.ListResult;
import com.chain.sketch.model.response.SingleResult;
import com.chain.sketch.repo.RoomJpaRepo;
import com.chain.sketch.repo.UserJpaRepo;
import com.chain.sketch.service.ResponseService;
import com.chain.sketch.webSocket.model.ChatRoom;
import com.chain.sketch.webSocket.repo.ChatRoomRepository;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Api(tags = {"3. Room"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class RoomController {
    private final RoomJpaRepo roomJpaRepo;
    private final UserJpaRepo userJpaRepo;
    private final ResponseService responseService;
    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public RoomController(ChatRoomRepository chatRoomRepository,
                          RoomJpaRepo roomJpaRepo,
                          UserJpaRepo userJpaRepo,
                          ResponseService responseService) {
        this.chatRoomRepository = chatRoomRepository;
        this.responseService = responseService;
        this.roomJpaRepo = roomJpaRepo;
        this.userJpaRepo = userJpaRepo; }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "게임 방 생성", notes = "새로운 공개 게임 방을 생성한다.")
    @PostMapping(value = "/room")
    public CommonResult createRoom(
            @ApiParam(value = "방 제목", required = true) @RequestParam String title,
            @ApiParam(value = "진행 라운드 수", required = true) @RequestParam Integer round,
            @ApiParam(value = "제한 시간", required = true) @RequestParam Integer limit
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        User user = userJpaRepo.findByUid(id).orElseThrow(UserNotFoundException::new);

        Optional<Room> room = roomJpaRepo.findByTitle(title);
        if(room.isPresent())
            throw new RoomExistException();

        roomJpaRepo.save(Room.builder()
                .title(title)
                .isSecret(false)
                .leaderName(user.getName())
                .allPeople(0)
                .readyPeople(1)
                .isPlaying(false)
                .limitTime(limit)
                .round(round)
                .build());

        chatRoomRepository.addChatRoom(ChatRoom.create(title));

        return responseService.getSuccessResult();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "비밀 게임 방 생성", notes = "새로운 게임 방을 생성한다. 게임 방제 중복 불가")
    @PostMapping(value = "/room/secret")
    public CommonResult createRoom(
            @ApiParam(value = "방 제목", required = true) @RequestParam String title,
            @ApiParam(value = "방 비밀번호") @RequestParam String password,
            @ApiParam(value = "진행 라운드 수", required = true) @RequestParam Integer round,
            @ApiParam(value = "제한 시간", required = true) @RequestParam Integer limit
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        User user = userJpaRepo.findByUid(id).orElseThrow(UserNotFoundException::new);

        Optional<Room> room = roomJpaRepo.findByTitle(title);
        if(room.isPresent())
            throw new RoomExistException();

        roomJpaRepo.save(Room.builder()
                .title(title)
                .isSecret(true)
                .password(password)
                .leaderName(user.getName())
                .allPeople(0)
                .readyPeople(1)
                .isPlaying(false)
                .limitTime(limit)
                .round(round)
                .build());

        chatRoomRepository.addChatRoom(ChatRoom.create(title));
        return responseService.getSuccessResult();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "특정 게임 방 정보 가져오기", notes = "게임방 제목으로 특정 게임방의 정보를 가져온다.")
    @GetMapping("/room")
    public SingleResult<Room> getOneRoom(@ApiParam(value = "방 제목", required = true) @RequestParam String title) {
        Room room = roomJpaRepo.findByTitle(title).orElseThrow(RoomNotFoundException::new);
        return responseService.getSingleResult(room);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "모든 게임 방 리스트 조회", notes = "모든 게임방을 조회함.")
    @GetMapping("/rooms")
    public ListResult<Room> getAllRooms() {
        return responseService.getListResult(roomJpaRepo.findAll());
    }
}
