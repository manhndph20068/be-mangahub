package com.example.bemangahub.service;

import com.example.bemangahub.ServiceResult;
import com.example.bemangahub.dto.req.RegisterRequest;
import com.example.bemangahub.dto.RegisterResponse;
import com.example.bemangahub.dto.res.UserInfo;
import com.example.bemangahub.entity.Account;


public interface IAccountService {
    ServiceResult<RegisterResponse> register(RegisterRequest registerRequest);

    Boolean exitsEmailCerdential(String email);

    ServiceResult<UserInfo> inforCerdentialAccount(String email);

    ServiceResult<UserInfo> inforSocialAccount(String email, String type);

    Account createSocialAccount(String email, String type);

    Account createCredentialAccount(String email,String password, String type);

    Boolean exitsEmailSocial(String email, String type);

    Account findByEmailAndPassword(String email, String password);
}
