package com.example.bemangahub.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginWithCerdential {
    private String accessToken;
    private String refreshToken;
    private UserInfo userInfo;
}
