package com.example.bemangahub.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken {
    private String token;
    private String email;
    private Instant expiryDate;
    private String type;
    private UserInfo userInfo;
}
