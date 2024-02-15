package com.example.bemangahub.service;

import com.example.bemangahub.ServiceResult;
import com.example.bemangahub.dto.RegisterRequest;
import com.example.bemangahub.dto.RegisterResponse;
import com.example.bemangahub.dto.res.UserInfo;
import org.springframework.stereotype.Service;


public interface IAccountService {
    ServiceResult<RegisterResponse> register(RegisterRequest registerRequest);

    Boolean exitsEmail(RegisterRequest registerRequest);

    ServiceResult<UserInfo> inforCerdentialAccount(String email);
}
