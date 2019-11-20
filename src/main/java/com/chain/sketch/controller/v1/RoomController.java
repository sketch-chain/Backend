package com.chain.sketch.controller.v1;


import com.chain.sketch.advice.exception.RoomExistException;
import com.chain.sketch.advice.exception.UserNotFoundException;
import com.chain.sketch.entity.Room;
import com.chain.sketch.entity.User;
import com.chain.sketch.model.response.CommonResult;
import com.chain.sketch.model.response.ListResult;
import com.chain.sketch.repo.RoomJpaRepo;
import com.chain.sketch.repo.UserJpaRepo;
import com.chain.sketch.service.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
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
                .leaderIdx(id)
                .allPeople(1)
                .readyPeople(1)
                .isPlaying(false)
                .limitTime(limit)
                .round(round)
                .build());

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
                .leaderIdx(id)
                .allPeople(1)
                .readyPeople(1)
                .isPlaying(false)
                .limitTime(limit)
                .round(round)
                .build());
        return responseService.getSuccessResult();
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
