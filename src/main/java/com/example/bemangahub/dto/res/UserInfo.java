package com.example.bemangahub.dto.res;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String username;
    private String email;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;
    private String refreshToken;
    private Integer isVerified;
    private String type;
    private String role;

}
