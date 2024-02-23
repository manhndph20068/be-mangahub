package com.example.bemangahub.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Integer idRole;
    private Integer idType;
}
