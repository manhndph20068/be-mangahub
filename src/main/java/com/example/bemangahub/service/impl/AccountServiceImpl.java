package com.example.bemangahub.service.impl;

import com.example.bemangahub.ServiceResult;
import com.example.bemangahub.config.AppConstant;
import com.example.bemangahub.dto.RegisterRequest;
import com.example.bemangahub.dto.RegisterResponse;
import com.example.bemangahub.dto.res.UserInfo;
import com.example.bemangahub.entity.Account;
import com.example.bemangahub.entity.Role;
import com.example.bemangahub.repository.AccountRepository;
import com.example.bemangahub.repository.RoleRepository;
import com.example.bemangahub.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ServiceResult<RegisterResponse> register(RegisterRequest registerRequest) {
        Optional<Role> optionalRole = roleRepository.findById(registerRequest.getIdRole());
        Account account = new Account();
        Role roleId = optionalRole.get();
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        account.setEmail(registerRequest.getEmail());
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        account.setStatus(0);
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setRole(roleId);
        account = accountRepository.save(account);
        return new ServiceResult(AppConstant.SUCCESS, "created succesfully!", account);
    }

    @Override
    public Boolean exitsEmail(RegisterRequest registerRequest) {
        Optional<UserInfo> optionalAccount = accountRepository.findCerdentialByEmail(registerRequest.getEmail());
        if (optionalAccount.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ServiceResult<UserInfo> inforCerdentialAccount(String email) {
        Optional<UserInfo> optionalAccount = accountRepository.findCerdentialByEmail(email);
        if (optionalAccount.isPresent()) {
            return new ServiceResult(AppConstant.SUCCESS, "get info account successfully!", optionalAccount.get());
        } else {
            return new ServiceResult(AppConstant.FAIL, "get info account failed!", null);
        }
    }
}
