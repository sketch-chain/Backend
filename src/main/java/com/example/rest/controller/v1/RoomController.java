package com.example.rest.controller.v1;

import com.example.rest.advice.exception.RoomExistException;
import com.example.rest.advice.exception.UserNotFoundException;
import com.example.rest.entity.Room;
import com.example.rest.entity.User;
import com.example.rest.model.response.CommonResult;
import com.example.rest.model.response.SingleResult;
import com.example.rest.repo.RoomJpaRepo;
import com.example.rest.repo.UserJpaRepo;
import com.example.rest.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Api(tags = {"3. Room"})
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class RoomController {
    private final RoomJpaRepo roomJpaRepo;
    private final UserJpaRepo userJpaRepo;
    private final ResponseService responseService;

    @ApiOperation(value = "게임 방 생성", notes = "새로운 게임 방을 생성한다. 게임 방제 중복 불가")
    @PostMapping(value = "/room")
    public CommonResult createRoom(
            @ApiParam(value = "방 제목", required = true) @RequestParam String title,
            @ApiParam(value = "비밀방", required = false) @RequestParam String  secret
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        User user = userJpaRepo.findByUid(id).orElseThrow(UserNotFoundException::new);

        Optional<Room> room = roomJpaRepo.findByTitle(title);
        if(room.isPresent())
            throw new RoomExistException();

        if(secret.equals("true")) {
            roomJpaRepo.save(Room.builder()
                    .title(title)
                    .isSecret(true)
                    .leaderIdx(id)
                    .allPeople(1)
                    .readyPeople(1)
                    .build());
        } else {
            roomJpaRepo.save(Room.builder()
                    .title(title)
                    .isSecret(false)
                    .leaderIdx(id)
                    .allPeople(1)
                    .readyPeople(1)
                    .build());
        }
        return responseService.getSuccessResult();
    }
}
