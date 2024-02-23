package com.example.bemangahub.service.impl;

import com.example.bemangahub.ServiceResult;
import com.example.bemangahub.config.AppConstant;
import com.example.bemangahub.dto.req.RegisterRequest;
import com.example.bemangahub.dto.RegisterResponse;
import com.example.bemangahub.dto.res.UserInfo;
import com.example.bemangahub.entity.Account;
import com.example.bemangahub.entity.Role;
import com.example.bemangahub.entity.Type;
import com.example.bemangahub.repository.AccountRepository;
import com.example.bemangahub.repository.RoleRepository;
import com.example.bemangahub.repository.TypeRepository;
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
    private TypeRepository typeRepository;

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
    public Boolean exitsEmailCerdential(String email) {
        Optional<UserInfo> optionalAccount = accountRepository.findCerdentialByEmail(email);
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

    @Override
    public ServiceResult<UserInfo> inforSocialAccount(String email, String type) {
        Optional<UserInfo> optionalAccount = accountRepository.findSocialByEmail(email,type);
        if (optionalAccount.isPresent()) {
            return new ServiceResult(AppConstant.SUCCESS, "get info account successfully!", optionalAccount.get());
        } else {
            return new ServiceResult(AppConstant.FAIL, "get info account failed!", null);
        }
    }

    @Override
    public Account createSocialAccount(String email, String type) {
        Optional<Type> optionalType = typeRepository.findByName(type);
        if(optionalType.isPresent()) {
            Account account = new Account();
            account.setEmail(email);
            account.setCreatedAt(new Date());
            account.setUpdatedAt(new Date());
            account.setStatus(0);
            account.setRole(roleRepository.findById(1).get());
            account.setIsVerified(0);
            account.setType(optionalType.get());
            account = accountRepository.save(account);
            return account;
        }else {
            return null;
        }

    }

    @Override
    public Account createCredentialAccount(String email, String password, String type) {
        Optional<Type> optionalType = typeRepository.findByName(type);
        if(optionalType.isPresent()) {
            Account account = new Account();
            account.setEmail(email);
            account.setCreatedAt(new Date());
            account.setUpdatedAt(new Date());
            account.setStatus(0);
            account.setRole(roleRepository.findById(1).get());
            account.setIsVerified(0);
            account.setType(optionalType.get());
            account.setPassword(passwordEncoder.encode(password));
            account = accountRepository.save(account);
            return account;
        }else {
            return null;
        }
    }


    @Override
    public Boolean exitsEmailSocial(String email, String type) {
        if (accountRepository.findSocialByEmail(email, type).isPresent()) {
            return true;
        } else {
            return false;
        }
    }
}
