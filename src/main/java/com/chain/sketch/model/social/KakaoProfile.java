package com.chain.sketch.model.social;

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
        private String profile_image = "https://cdn.psychologytoday.com/sites/default/files/styles/article-inline-half/public/blogs/334/2012/07/101524-98975.jpg?itok=LGLD7Rgi";
    }
}
