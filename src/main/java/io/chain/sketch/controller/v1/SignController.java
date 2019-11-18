package io.chain.sketch.controller.v1;

import io.chain.sketch.advice.exception.UserExistException;
import io.chain.sketch.advice.exception.UserNotFoundException;
import io.chain.sketch.config.security.JwtTokenProvider;
import io.chain.sketch.entity.User;
import io.chain.sketch.model.response.CommonResult;
import io.chain.sketch.model.response.SingleResult;
import io.chain.sketch.model.social.KakaoProfile;
import io.chain.sketch.repo.UserJpaRepo;
import io.chain.sketch.service.ResponseService;
import io.chain.sketch.service.social.KakaoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class SignController {

    private final UserJpaRepo userJpaRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final KakaoService kakaoService;

    @ApiOperation(value = "소셜 로그인", notes = "소셜 회원 로그인을 한다.")
    @PostMapping(value = "/signin")
    public SingleResult<String> signin(
            @ApiParam(value = "소셜 access_token", required = true) @RequestParam String accessToken) {

        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
        User user = userJpaRepo.findByUid(String.valueOf(profile.getId())).orElseThrow(UserNotFoundException::new);
        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user.getIdx()), user.getRoles()));
    }

    @PostMapping(value = "/signup")
    public CommonResult signup(
            @ApiParam(value = "소셜 access_token", required = true) @RequestParam String accessToken
    ) {
        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
        Optional<User> user = userJpaRepo.findByUid(String.valueOf(profile.getId()));
        if(user.isPresent())
            throw new UserExistException();

        userJpaRepo.save(User.builder()
                .uid(String.valueOf(profile.getId()))
                .name(profile.getNickname())
                .profileImage(profile.getProfileImage())
                .roles(Collections.singletonList("ROLE_USER"))
                .coin(0)
                .exp(0)
                .score(0)
                .level(1)
                .build());
        return responseService.getSuccessResult();
    }
}
