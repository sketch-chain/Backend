package io.chain.sketch.model.social;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
// Kakao 유저 정보를 담음
public class KakaoProfile {
    private Long id;
    private Properties properties;

    public String getNickname() {
        return this.getProperties().getNickname();
    }

    public String getProfileImage() {
        return this.getProperties().getProfile_image();
    }

    @Getter
    @Setter
    @ToString
    private static class Properties {
        private String nickname;
        private String thumbnail_image;
        private String profile_image;
    }
}
